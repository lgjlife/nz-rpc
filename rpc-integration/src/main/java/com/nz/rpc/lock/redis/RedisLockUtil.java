package com.nz.rpc.lock.redis;


import com.nz.rpc.lock.Lock;
import com.nz.rpc.lock.exception.RequestLockException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *功能描述 
 * @author lgj
 * @Description 　　Redis 实现分布式锁
 *
 * 设计思想
 * 1.　setnx+超时　设置
 * 　　如果返回ＯＫ，说明获取锁成功，否则失败
 *
 * 2.  如何防止unlock删除的是别人的锁
 * 　　set-value,value为版本号，版本号一致才能删除锁
 *
 * 3. 集群情况下，如果写节点的锁还没有复制到其他节点，就已经宕积，如何防止?
 * 　　可以向多个节点请求锁,确定多个节点都返回成功才能够申请到锁
 *
 * 4. 超时时间到,但是任务还没有完成，如何解决?
 * 　　申请锁的时候，开一个线程，线程定时检测任务是否完成，未完成则重新设置超时时间
 * 5. 　tryLock　阻塞式获取锁
 *
 * 6.　可重入锁如何实现
 *
 * 7.　公平锁如何实现
 *
 *  8.　原子性问题
 * 　　使用LUA脚本
 * @date 4/19/19
*/
@Data
@Slf4j
public class RedisLockUtil implements Lock {

    private RedisClient redisClient;

    private  final  String SET_SUCCESS = "OK";
    //private RedisPoolClient redisPoolClient;

    //用于保存当前线程的请求锁的原　锁名称,锁value，重入锁技计数器
    private ConcurrentHashMap<Thread, List<LockData>> lockDataMap = new ConcurrentHashMap<>();
    private AtomicInteger lockUid = new AtomicInteger(0);


    /**
     *功能描述 
     * @author lgj
     * @Description 请求锁,timeoutMs事件内未获取到锁则返回false
     * @date 4/19/19
     * @param: 　lockKey：　锁的键值，每个调用方法应设者不一样，同一个线程和相同的lockKey则判定为重入锁
     *           millisecondsToExpire : 锁超时时间--ms，因为使用新建线程来续期，新建线程启动时间不确定，因此该值应当设置大些，否则超时时间到但任务未完成导致未能续期
     *           timeoutMs: 等待时间－－ms，超时时间到，还未申请到锁，返回false
     *
     * @return: 　true : 申请成功　　false: 申请失败
     *
    */
    @Override
    public boolean tryLock(String lockKey,int millisecondsToExpire,int timeoutMs) throws  Exception {


        if((lockKey == null)
            && (millisecondsToExpire<0)
            && (timeoutMs < 0 )){
            throw  new IllegalArgumentException();
        }

        //重入锁判定，根据thread和lockKey判定是否是同一个锁
        if(checkRetansLock(lockKey)){
            return true;
        }

        String lockValue =  lockUid.incrementAndGet()+"";

        String reult = redisClient.setNxValue(lockKey,lockValue,millisecondsToExpire);//


        //请求锁失败
        if(reult == null){
            //log.info("请求锁[{}－{}]失败",lockKey,value);
            try{
                //sleepTime　会影响性能
                int sleepTime = 10 ;
                int sleepSum = 0;
                while (true){
                    //这个时间会影响性能
                    Thread.sleep(sleepTime);
                    sleepSum +=sleepTime;
                    if(sleepSum >= timeoutMs){
                        log.info("请求锁[{}－{}]超时，失败！",lockKey,lockValue,(timeoutMs-sleepSum));
                        return  false;
                    }

                    if(SET_SUCCESS.equals(redisClient.setNxValue(lockKey,lockValue,millisecondsToExpire))){
                        log.info("请求锁[{}－{}]成功",lockKey,lockValue);
                        break;
                    }
                }
            }
            catch(Exception ex){
               // log.error("请求锁[{}－{}]失败:",lockKey,lockValue,ex.getMessage());
                throw new RequestLockException("请求请求Redis锁失败！",ex.getCause());

            }
        }
        log.debug("请求锁[{}]－[{}]成功",lockKey,lockValue);
        //用该线程不断检测过期时间，过期时间将要到，任务没完成，则重新设置超时时间
        new LockThread(lockKey,lockValue,millisecondsToExpire).start();

        createLockData(lockKey,lockValue);
        return  true;


    }
    private  void  createLockData(String lockKey,String lockValue){

        LockData lockData =  new LockData(lockKey,lockValue,new AtomicInteger(1));
        List<LockData> lockData1 =  lockDataMap.get(Thread.currentThread());
        if(lockData1  == null){
            lockData1 = new ArrayList<>();

        }
        lockData1.add(lockData);
        lockDataMap.put(Thread.currentThread(),lockData1);

    }

    /**
     *功能描述
     * @author lgj
     * @Description  检测是否是重入锁
     * @date 4/22/19
     * @param:
     * @return:
     *
    */
    private boolean checkRetansLock(String lockKey){
        List<LockData> lockDatas = lockDataMap.get(Thread.currentThread());
        //判断是否是重入锁
        if(lockDatas !=  null){

            List<LockData> selectlockData =  lockDatas.stream().filter((val)-> val.lockKey.equals(lockKey)).collect(Collectors.toList());
            if(selectlockData.size() != 0){
                //重入锁
                selectlockData.get(0).lockCount.incrementAndGet();
                List<LockData> filter = lockDatas.stream().filter((val)-> !val.lockKey.equals(lockKey)).collect(Collectors.toList());
                filter.add(selectlockData.get(0));
                lockDataMap.put(Thread.currentThread(),filter);
                return true;
            }

        }
        return false;

    }

    /**
     *功能描述
     * @author lgj
     * @Description 　　释放锁
     * 　　　　　　　　　判断删除的锁和当前持有的锁是否是同一个锁
     * @date 4/19/19
     * @param:　　
     * @return:
     *
    */
    @Override
    public void unlock(String lockKey) {

        List<LockData> lockDatas = lockDataMap.get(Thread.currentThread());
        if(lockDatas != null){
            List<LockData> selectlockData =  lockDatas.stream().filter((val)-> val.lockKey.equals(lockKey)).collect(Collectors.toList());
            if(selectlockData.size() == 0){
                log.info("从未获取过该锁[{}－{}]",lockKey);
                return;
            }

            int count = selectlockData.get(0).lockCount.decrementAndGet();

            //log.info("selectlockData：　" + selectlockData.get(0));
            if(count == 0){
                // String value =   this.lockValue.get();
                String value = selectlockData.get(0).lockValue;
                if(value != null){
                    String script = "local result "
                            +" if(redis.call('get',KEYS[1]) == ARGV[1]) then "
                            //如果锁还存在,则删除锁状态
                            + " result=redis.call('del',KEYS[1])"
                            + " end"
                            //返回删除锁状态
                            + " return result";
                    log.info("锁[{}-{}]删除.... ",lockKey,value);
                    String[] params = new String[2];
                    params[0] = lockKey;
                    params[1] = value;

                    Object result = redisClient.eval(script,1,params);
                    if(result == null){
                        log.info("锁[{}]没有获取到，或者已经被删除，删除失败！",lockKey,result);
                        return;
                    }
                    log.info("锁[{}-{}]删除结果result =[{}] ",lockKey,value,result);
                }

            }

            List<LockData> filter = lockDatas.stream().filter((val)-> !val.lockKey.equals(lockKey)).collect(Collectors.toList());
            filter.add(selectlockData.get(0));
            lockDataMap.put(Thread.currentThread(),filter);


        }

    }

    @ToString
    @AllArgsConstructor
    private static class LockData{

        //锁key  －》　lockName
        //
        private String lockKey;
        //
        private String lockValue;
        //可重入锁计数器,初始0，每申请一次锁加１,解锁时为0才能删除节点
        private AtomicInteger lockCount;
    }


    /**
     *功能描述
     * @author lgj
     * @Description  任务时间太长导致过期时间不足，该线程一置检测，任务没完成则重新设置过期时间
     * @date 4/20/19
    */
    class LockThread extends Thread{

        private  String lockey;
        private int millisecondsToExpire;
        private  String lockValue;



        public LockThread(String lockey, String lockValue, int millisecondsToExpire) {
            this.lockey = lockey;
            this.lockValue = lockValue;
            this.millisecondsToExpire = millisecondsToExpire;
        }

        @Override
        public void run() {

            while(true){
               try{
                   Thread.sleep(millisecondsToExpire*3/4);

                   //String val = redisClient.get(lockey);
                   //log.info("[{}] - > {}",lockey,val);
                   String script = " if(redis.call('get',KEYS[1]) == ARGV[1]) then "
                           //如果锁还存在,则查看超时时间
                           + " redis.call('pexpire',KEYS[1],ARGV[2])"
                           + " return 'true'"
                           + " else"
                           //锁已经被删除
                           + " return 'false'"
                           + " end";
                   String[] params = new String[3];
                   params[0] = lockey;
                   params[1] = lockValue;
                   params[2] = String.valueOf(millisecondsToExpire);
                   Object result =  redisClient.eval(script,1,params);
                   if(result.equals("false")){
                       log.info("锁[{}－{}]已经被删除！",lockey,lockValue);
                       return;
                   }
                   else {
                       log.info("锁[{}－{}]重新设置超时时间[{}]！",lockey,lockValue,millisecondsToExpire);
                   }

               }
               catch(Exception ex){
                   log.error(ex.getMessage());
               }
            }

        }
    }
}

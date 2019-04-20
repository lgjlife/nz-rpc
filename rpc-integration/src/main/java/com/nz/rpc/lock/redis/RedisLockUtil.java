package com.nz.rpc.lock.redis;


import com.nz.rpc.lock.Lock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.params.SetParams;

import java.util.Random;

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
    private  int DEFAULT_TIMEOUT_MS = 10000;
    private  final  String SET_SUCCESS = "OK";
    private ThreadLocal<String> lockValue = new ThreadLocal<String>();
    private RedisPoolClient redisPoolClient;

    /**
     *功能描述
     * @author lgj
     * @Description 请求锁，如果失败，则直接返回
     * @date 4/19/19
     * @param:　lockKey：锁的key；　lockValue：锁的唯一id
     *
     * @return:　true：请求锁成功　　false：请求锁失败
     *
    */
    @Override
    public boolean lock(String lockKey,int millisecondsToExpire) {

        String value = new Random().nextInt(1000) + Thread.currentThread().getName()+new Random().nextInt(1000);
        this.lockValue.set(value);

        String reult = redisClient.setNxValue(lockKey,value,millisecondsToExpire);
        //请求锁失败
        if(reult == null){
            log.info("请求锁[{}-{}]失败",lockKey,value);
            return  false;
        }
        else if(reult.equals(SET_SUCCESS)){
            log.info("请求锁[{}]成功,value={}",lockKey,value);
            //用该线程不断检测过期时间，过期时间将要到，任务没完成，则重新设置超时时间
            new LockThread(lockKey,value,millisecondsToExpire).start();
            return  true;

        }

        return  false;
    }

    /**
     *功能描述 
     * @author lgj
     * @Description 请求锁,timeoutMs事件内未获取到锁则返回false
     * @date 4/19/19
     * @param: 
     * @return: 
     *
    */
    @Override
    public boolean tryLock(String lockKey,int millisecondsToExpire,int timeoutMs) {

        String value = new Random().nextInt(1000) + Thread.currentThread().getName()+new Random().nextInt(1000);
        this.lockValue.set(value);
        SetParams setParams = SetParams.setParams().nx().px(millisecondsToExpire);
//redisClient.setNxValue(lockKey,value,millisecondsToExpire);//
        String reult = redisPoolClient.getJedis().set(lockKey,value,setParams);

        //请求锁失败
        if(reult == null){
            log.info("请求锁[{}－{}]失败",lockKey,value);
            try{
                int sleepTime = 20 ;
                int sleepSum = 0;
                while (true){
                    //这个时间会影响性能
                    Thread.sleep(sleepTime);
                    sleepSum +=sleepTime;
                    if(sleepSum >= timeoutMs){
                        log.info("请求锁[{}－{}]超时",lockKey,value,(timeoutMs-sleepSum));
                        return  false;
                    }
                    String reult1 = redisClient.setNxValue(lockKey,value,millisecondsToExpire);
                    if(reult1 == null){
                        log.info("请求锁[{}－{}]失败,还剩[{}]ms时间等待",lockKey,value,(timeoutMs-sleepSum));
                    }
                    else if(reult1.equals(SET_SUCCESS)){
                        log.info("请求锁[{}－{}]成功",lockKey,value);
                        //用该线程不断检测过期时间，过期时间将要到，任务没完成，则重新设置超时时间
                        new LockThread(lockKey,value,millisecondsToExpire).start();
                        return  true;
                    }

                }
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }


            return  false;
        }
        else if(reult.equals(SET_SUCCESS)){
            log.debug("请求锁[{}]成功",lockKey);
            //用该线程不断检测过期时间，过期时间将要到，任务没完成，则重新设置超时时间
            new LockThread(lockKey,value,millisecondsToExpire).start();
            return  true;

        }

        return  false;


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
        String value =   this.lockValue.get();
        if(value != null){
            String script = "local result "
                    +" if(redis.call('get',KEYS[1]) == ARGV[1]) then "
                    //如果锁还存在,则查看超时时间
                    + " result=redis.call('del',KEYS[1])"
                    + " end"
                    //返回删除锁状态
                    + " return result";
           Object result = redisClient.jedis().eval(script,1,lockKey,value);
           log.info("锁[{}]删除结果result =[{}] ",lockKey,result);
        }
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

                   String script = " if(redis.call('get',KEYS[1]) == ARGV[1]) then "
                           //如果锁还存在,则查看超时时间
                           + " redis.call('expire',KEYS[1],ARGV[2])"
                           + " return 'true'"
                           + " else"
                           //锁已经被删除
                           + " return 'false'"
                           + " end";
                   Object result =  redisClient.jedis().eval(script,1,lockey,lockValue,String.valueOf(millisecondsToExpire));
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
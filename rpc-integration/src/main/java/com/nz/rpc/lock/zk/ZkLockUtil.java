package com.nz.rpc.lock.zk;

import com.nz.rpc.lock.Lock;
import com.nz.rpc.zk.ZkCreateConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 *功能描述
 * @author lgj
 * @Description  zookeeper 实现分布式锁
 * @date 4/20/19
*/
@Slf4j
@Data
public class ZkLockUtil implements Lock {


    private  ZkClient zkClient;


    private ThreadLocal<Map<Thread,String>> lockPathMap =  new ThreadLocal();

    //用于保存当前线程的请求锁的原　锁名称,节点路径，重入锁技计数器
    private ConcurrentHashMap<Thread,List<LockData>>  lockDataMap = new ConcurrentHashMap<>();

    //锁路径　LOCK_ROOT_PATH + LOCKNAME + LOCKPATH_SEPARATOR + NUMS;
    //　－》　/locks/lockName_0000001
    private final String LOCK_ROOT_PATH = "/locks";
    private final String LOCKPATH_SEPARATOR = "-";


    /**
     *功能描述
     * @author lgj
     * @Description
     * @date 4/20/19
     * @param:
     * @return:
     *
    */
    public boolean lock(String lockKey, int millisecondsToExpire) {

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

        //不是重入锁
        //创建节点　节点　/locks/lockname_000000000001
        ZkCreateConfig config =  ZkCreateConfig.builder()
                .createMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .path(getCreatePath(lockKey))
                .build();
        //当前锁路径　-> /locks/lockname_000000013
        String curLockPath = zkClient.createPath(config);
        long curPathNum = Long.valueOf(curLockPath.split(LOCKPATH_SEPARATOR)[1]);

        //获取/locks子节点列表　size >= 1
        List<String> subRootPaths =  zkClient.getChildren(LOCK_ROOT_PATH);
        log.info("获取[{}]子节点列表:{}",LOCK_ROOT_PATH,subRootPaths);
        //只有一个节点，说明只有刚才建立的节点,没有其他进程线程获取锁,设置LockData后便返回
        if(subRootPaths.size() == 1){
            log.info("[{}]获取锁",subRootPaths.get(0));
            LockData lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
            List<LockData> lockData1 =  lockDataMap.get(Thread.currentThread());
            if(lockData1  == null){
                lockData1 = new ArrayList<>();

            }
            lockData1.add(lockData);
            lockDataMap.put(Thread.currentThread(),lockData1);
            return true;
        }
        //有其他进程线程已经获取锁
        else{
            TreeMap<Long,String> sortMap = new TreeMap<>();

            for (String subRootPath:subRootPaths){
                String[] sub = subRootPath.split(LOCKPATH_SEPARATOR);
                if(sub[0].equals(lockKey)){
                    sortMap.put(Long.valueOf(sub[1]),subRootPath);
                }

            }
            //subMap存储的最后元素正是比当前小的前一个节点
            SortedMap<Long,String> subMap = sortMap.subMap(0L,curPathNum);

            if (subMap.size() == 0){
                //第一个获取锁,直接返回
                LockData lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
                List<LockData> lockData1 =  lockDataMap.get(Thread.currentThread());
                if(lockData1  == null){
                    lockData1 = new ArrayList<>();

                }
                lockData1.add(lockData);
                lockDataMap.put(Thread.currentThread(),lockData1);
                return true;

            }

            //前一个节点
            String prevNodePath = null;
            if(subMap.size() != 0){
                Long key = subMap.lastKey();
                prevNodePath = subMap.get(key);
            }
            log.info("[{}]前一个节点 [{}]",curLockPath,prevNodePath);
            //创建一个计数器
            CountDownLatch countDownLatch = new CountDownLatch(1);

            if(prevNodePath != null){
                //添加监听器
                zkClient.checkExistsAndAddWatch(getWatcherPath(prevNodePath),new Thread(){
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                    }
                });

                try{
                    log.info("等待节点[{}]释放.......",prevNodePath);
                    countDownLatch.await();
                    log.info("节点[{}]释放! 获取锁[{}]",prevNodePath,curLockPath);

                    LockData lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
                    List<LockData> lockData1 =  lockDataMap.get(Thread.currentThread());
                    if(lockData1  == null){
                        lockData1 = new ArrayList<>();

                    }
                    lockData1.add(lockData);
                    lockDataMap.put(Thread.currentThread(),lockData1);
                    return true;
                }
                catch(Exception ex){
                    log.error(ex.getMessage());
                }

            }
        }

        return false;

    }




    public boolean tryLock(String lockStr, int millisecondsToExpire, int timeoutMs) {
        return false;
    }

    /**
     *功能描述
     * @author lgj
     * @Description  解锁操作
     * @date 4/20/19
     * @param:
     * @return:
     *
    */
    public void unlock(String lockKey) {

        List<LockData> lockDatas = lockDataMap.get(Thread.currentThread());
        if(lockDatas != null){
            List<LockData> selectlockData =  lockDatas.stream().filter((val)-> val.lockKey.equals(lockKey)).collect(Collectors.toList());
            if(selectlockData.size() == 0){
                log.info("从未获取过该锁[{}－{}]",lockKey);
                return;
            }

            int count = selectlockData.get(0).lockCount.decrementAndGet();

            if(count == 0){
                log.info("释放锁,删除[{}]",selectlockData.get(0).createLockPath);
                zkClient.remove(selectlockData.get(0).createLockPath);
            }

            List<LockData> filter = lockDatas.stream().filter((val)-> !val.lockKey.equals(lockKey)).collect(Collectors.toList());
            filter.add(selectlockData.get(0));
            lockDataMap.put(Thread.currentThread(),filter);


        }



    }

    private String getCreatePath(String subPath){
        return new StringBuilder().append(LOCK_ROOT_PATH).append("/").append(subPath).append(LOCKPATH_SEPARATOR).toString();
    }
    private String getWatcherPath(String subPath){
        return new StringBuilder().append(LOCK_ROOT_PATH).append("/").append(subPath).toString();
    }

    @ToString
    @AllArgsConstructor
    private static class LockData{

        //锁key  －》　lockName
        //lock(lockName)
        private String lockKey;
        //zookeeper 中的锁节点路径　/locks/lockName_0000000001
        private String createLockPath;
        //可重入锁计数器,初始0，每申请一次锁加１,解锁时为0才能删除节点
        private AtomicInteger lockCount;
    }

}

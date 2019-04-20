package com.nz.rpc.lock.zk;

import com.nz.rpc.zk.ZkCreateConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *功能描述
 * @author lgj
 * @Description  zookeeper 实现分布式锁
 * @date 4/20/19
*/
@Slf4j
@Data
public class ZkLockUtil {


    private  ZkClient zkClient;

    private ThreadLocal<Thread> curThread =  new ThreadLocal();
    private ThreadLocal<Map<Thread,String>> lockPathMap =  new ThreadLocal();

    private ConcurrentHashMap<Thread,LockData>  lockDataMap = new ConcurrentHashMap<>();

    //锁路径　LOCK_ROOT_PATH + LOCKNAME + LOCKPATH_SEPARATOR + NUMS;
    //　－》　/locks/lockName_0000001
    private final String LOCK_ROOT_PATH = "/locks";
    private final String LOCKPATH_SEPARATOR = "-";


    public boolean lock(String lockKey, int millisecondsToExpire) {

        LockData lockData = lockDataMap.get(Thread.currentThread());
        if((lockData != null)
                &&(lockData.lockKey.equals(lockKey))){
            //重入锁
            lockData.lockCount.incrementAndGet();
            return true;
        }


        //创建节点
        ZkCreateConfig config =  ZkCreateConfig.builder()
                .createMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .path(getCreatePath(lockKey))
                .build();
        //当前锁路径　-> /locks/lockname_00000000001
        String curLockPath = zkClient.createPath(config);
        long curPathNum = Long.valueOf(curLockPath.split(LOCKPATH_SEPARATOR)[1]);

        //获取节点列表　size >= 1
        List<String> subRootPaths =  zkClient.getChildren(LOCK_ROOT_PATH);
        log.info("获取[{}]子节点列表:{}",LOCK_ROOT_PATH,subRootPaths);
        //只有　一个节点，说明没有其他进程线程获取锁
        if(subRootPaths.size() == 1){
            log.info("[{}]获取锁",subRootPaths.get(0));
            lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
            lockDataMap.put(Thread.currentThread(),lockData);
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
                lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
                lockDataMap.put(Thread.currentThread(),lockData);
                return true;

            }

            //前一个节点
            String prevNodePath = null;


            if(subMap.size() != 0){
                Long key = subMap.lastKey();
                prevNodePath = subMap.get(key);
            }
            log.info("[{}]前一个节点 [{}]",curLockPath,prevNodePath);
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
                    curThread.set(Thread.currentThread());
                    lockData =  new LockData(lockKey,curLockPath,new AtomicInteger(1));
                    lockDataMap.put(Thread.currentThread(),lockData);
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


    public void unlock(String lockKey) {

        LockData lockData = lockDataMap.get(Thread.currentThread());
        if(lockData == null){
            log.info("从未获取过该锁[{}－{}]",lockKey);
            return;
        }
        int count = lockData.lockCount.decrementAndGet();
        if(count == 0){
            log.info("释放锁,删除[{}]",lockData.createLockPath);
            zkClient.remove(lockData.createLockPath);
        }

    }

    private String getCreatePath(String subPath){
        return new StringBuilder().append(LOCK_ROOT_PATH).append("/").append(subPath).append(LOCKPATH_SEPARATOR).toString();
    }
    private String getWatcherPath(String subPath){
        return new StringBuilder().append(LOCK_ROOT_PATH).append("/").append(subPath).toString();
    }

    @AllArgsConstructor
    private static class LockData{

        private String lockKey;
        private String createLockPath;
        private AtomicInteger lockCount;



    }
}

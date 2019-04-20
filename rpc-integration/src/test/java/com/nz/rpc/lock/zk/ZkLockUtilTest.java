package com.nz.rpc.lock.zk;


import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;


@Slf4j
public class ZkLockUtilTest {

    @Test
    public void zkLockTest(){

      ZkLockService zkLockService =  new ZkLockService();
        /*   zkLockService.func();*/
       // zkLockService.func();
       // zkLockService.func();

        for(int i = 0; i< 1; i++){

            new Thread(){
                @Override
                public void run() {
                    zkLockService.func();

                }
            }.start();
        }

        while (true);

    }

    //@Test
    public  void InterProcessMutexTest(){

        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181",
                retryPolicy);
        client.start();
        InterProcessMutex mutex = new InterProcessMutex(client,"/aa");

        try{

            mutex.acquire();
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            try{

                mutex.release();
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }
        }

    }

}

class MyThread{


}
package com.nz.rpc.lock.zk;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class ZkLockService {

    private ZkLockUtil zkLockUtil =  ZkClientFatory.zkLockUtil();

    public void func(){



        String lockName = "func";

        String lockPath = null;
        try{
            zkLockUtil.lock(lockName,100);

            log.info("＋＋＋＋＋＋  func 开始执行任务＋＋＋＋＋＋＋");
            Thread.sleep(new Random().nextInt(1000));
            log.info("＋＋＋＋＋＋ func 任务执行完毕＋＋＋＋＋＋＋");
            func1();
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            zkLockUtil.unlock(lockName);
        }
    }

    public   void func1(){


        String lockName = "func";

        String lockPath = null;
        try{
            zkLockUtil.lock(lockName,100);
            log.info("＋＋＋＋＋＋  func1 开始执行任务＋＋＋＋＋＋＋");
            Thread.sleep(new Random().nextInt(1000));
            log.info("＋＋＋＋＋＋ func1 任务执行完毕＋＋＋＋＋＋＋");
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            zkLockUtil.unlock(lockPath);
        }
    }

}

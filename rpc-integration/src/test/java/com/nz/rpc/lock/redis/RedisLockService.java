package com.nz.rpc.lock.redis;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisLockService {

    private  RedisLockUtil lockUtil =  RedisFatory.redisLockUtil();

    public void func1(){

        try{

            if(lockUtil.tryLock("lockTest",200,30000) == false){
                log.info("申请锁失败......");
            }
            //模拟任务执行
            log.info("任务开始执行............");
            Thread.sleep(50);
            log.info("任务执行结束............");
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally {
            lockUtil.unlock("lockTest");
        }




    }
}

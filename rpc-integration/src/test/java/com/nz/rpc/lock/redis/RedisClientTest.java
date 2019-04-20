package com.nz.rpc.lock.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.params.SetParams;

import java.util.Random;

@Slf4j
public class RedisClientTest {

    //@Test
    public void run(){



        /*RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedisClient(redisClient);
             redisLockUtil.lock("lock");*/

        String result = RedisFatory.jedis().set("key2",
                "value",
                SetParams.setParams().nx().ex(20));
        log.info("result = " + result);


        String script = "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}";

        Object object = RedisFatory.jedis().eval(script,2,"aa","bb","cc","dd");

        System.out.println(object);

        //////////////

        String script1 = "local msg = 'Hello, world!'" +
                "return msg";
        Object object1 = RedisFatory.jedis().eval(script1);

        System.out.println(object1);

        //////////////

        script = "local val = redis.call('get','lua-key') return val";
        object = RedisFatory.jedis().eval(script);
        System.out.println(object);
      //  script = "local val = redis.call('set',KEY[1],ARGV[1]) ";
      //   jedis().eval(script,1,"LUA-KEY","100");
        //System.out.println(object);

        String script2 ="local val = redis.call('get',KEYS[1])"
                + "return val";
        Object object2 = RedisFatory.jedis().eval(script2, 1,"lua-key");
        System.out.println(object2);





    }


   // @Test
    public void  lockTest(){

        RedisLockUtil lockUtil =  RedisFatory.redisLockUtil();
        lockUtil.tryLock("lockTest",200,10000);

        //模拟任务执行
        try{
            Thread.sleep(new Random().nextInt(400));
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

        lockUtil.unlock("lockTest");

        try{
            Thread.sleep(100000);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

    }

    @Test
    public void expire() {
        String result = RedisFatory.jedis().set("aa0",
                "value",
                SetParams.setParams().nx().px(1000));
        log.info("result = " + result);

        RedisFatory.redisClient().setNxValue("aa","bb",1000);
    }



}
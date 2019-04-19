package com.nz.rpc.lock.redis;

import com.nz.rpc.lock.LockProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Slf4j
public class RedisClientTest {

    //@Test
    public void run(){



        /*RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedisClient(redisClient);
             redisLockUtil.lock("lock");*/

        String result = jedis().set("key2",
                "value",
                SetParams.setParams().nx().ex(20));
        log.info("result = " + result);


        String script = "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}";

        Object object = jedis().eval(script,2,"aa","bb","cc","dd");

        System.out.println(object);

        //////////////

        String script1 = "local msg = 'Hello, world!'" +
                "return msg";
        Object object1 = jedis().eval(script1);

        System.out.println(object1);

        //////////////

        script = "local val = redis.call('get','lua-key') return val";
        object = jedis().eval(script);
        System.out.println(object);
      //  script = "local val = redis.call('set',KEY[1],ARGV[1]) ";
      //   jedis().eval(script,1,"LUA-KEY","100");
        //System.out.println(object);

        String script2 ="local val = redis.call('get',KEYS[1])"
                + "return val";
        Object object2 = jedis().eval(script2, 1,"lua-key");
        System.out.println(object2);





    }


    @Test
    public void  lockTest(){

        RedisLockUtil lockUtil =  redisLockUtil();
        lockUtil.tryLock("lockTest",200,10000);

        try{
            Thread.sleep(100);
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

    //@Test
    /*public void expideAr() {
        String result = jedis().set("key2",
                "value",
                SetParams.setParams().nx().px(200));
        log.info("result = " + result);
    }*/



    public   RedisLockUtil redisLockUtil(){
        LockProperties  properties =  getLockProperties();
        log.info("properties:{}",properties);
        RedisPoolClient redisPoolClient = new RedisPoolClient(properties);
        RedisClient redisClient =  new RedisClient();
        redisClient.setRedisPoolClient(redisPoolClient);
        RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedisClient(redisClient);
        redisLockUtil.setRedisPoolClient(redisPoolClient);

        return  redisLockUtil;

    }

    LockProperties getLockProperties(){
        LockProperties properties =  new LockProperties();
        properties.setHost("127.0.0.1");
        properties.setPort(6379);
        properties.setMaxIdle(50);
        properties.setMaxActive(100);
        properties.setMinIdle(50);
        properties.setMaxWait(-1);
        return  properties;
    }


    public Jedis jedis(){
        LockProperties  properties =  getLockProperties();
        log.info("properties:{}",properties);
        RedisPoolClient redisPoolClient = new RedisPoolClient(properties);
        RedisClient redisClient =  new RedisClient();
        redisClient.setRedisPoolClient(redisPoolClient);

        return redisPoolClient.getJedis();
    }

}
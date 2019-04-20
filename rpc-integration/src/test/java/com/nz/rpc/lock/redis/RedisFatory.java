package com.nz.rpc.lock.redis;

import com.nz.rpc.lock.LockProperties;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;


@Slf4j
public class RedisFatory {

    public static LockProperties getLockProperties(){
        LockProperties properties =  new LockProperties();
        properties.setHost("127.0.0.1");
        properties.setPort(6379);
        properties.setMaxIdle(50);
        properties.setMaxActive(100);
        properties.setMinIdle(50);
        properties.setMaxWait(-1);
        return  properties;
    }

    public static  RedisPoolClient redisPoolClient(){
        LockProperties  properties =  getLockProperties();
        RedisPoolClient redisPoolClient = new RedisPoolClient(properties);
        return  redisPoolClient;
    }


    public  static  RedisClient redisClient(){
        RedisClient redisClient =  new RedisClient();
        redisClient.setRedisPoolClient(redisPoolClient());
        return  redisClient;
    }

    public  static  RedisLockUtil redisLockUtil(){
        RedisLockUtil redisLockUtil = new RedisLockUtil();
        redisLockUtil.setRedisClient(redisClient());
        redisLockUtil.setRedisPoolClient(redisPoolClient());
        return redisLockUtil;
    }


    public static  Jedis jedis(){
        return redisPoolClient().getJedis();
    }
}

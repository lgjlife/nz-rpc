package com.nz.rpc.lock.redis;

import com.nz.rpc.lock.LockProperties;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RedisPoolClient {

    private LockProperties lockProperties;
    private JedisPoolConfig  config;
    private JedisPool jedisPool;
    private  AtomicInteger poolCount = new AtomicInteger(0);

    public RedisPoolClient(LockProperties lockProperties) {
        this.lockProperties = lockProperties;

        init();
    }



    public void init(){

        config = new JedisPoolConfig();
        //  GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(lockProperties.getMaxActive());
        config.setMaxIdle(lockProperties.getMaxIdle());
        config.setMinIdle(lockProperties.getMinIdle());
        config.setMaxWaitMillis(lockProperties.getMaxWait());
        jedisPool = new JedisPool(config,lockProperties.getHost(),lockProperties.getPort(),2000);


    }

    public Jedis getJedis(){
        Jedis jedis =  new Jedis();
        jedis.connect();
       /* log.info("jedisPool-{}:NumActive={},NumIdle={},NumWaiters={}",
                poolCount.incrementAndGet(),
                jedisPool.getNumActive(),
                jedisPool.getNumIdle(),
                jedisPool.getNumWaiters());*/

        return jedisPool.getResource();
    }




}

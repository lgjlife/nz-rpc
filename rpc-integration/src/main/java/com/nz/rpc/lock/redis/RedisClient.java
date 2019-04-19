package com.nz.rpc.lock.redis;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.serialization.AbstractSerialize;
import com.nz.rpc.serialization.JdkSerializeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Data
@Slf4j
public class RedisClient {

    private AbstractSerialize serialize = new JdkSerializeUtil();

    private RedisPoolClient redisPoolClient;


    public  String setValue(String key,Object object){
        //String value = serialize.serialize(object);
        String value = JSON.toJSONString(object);
        String result = jedis().set(key,value);
        return  result;//SetParams
    }

    public  String setValue(String key,Object object,int millisecondsToExpire){

        String value = JSON.toJSONString(object);
        String result = jedis().set(key,value,SetParams.setParams().px(millisecondsToExpire));
        return  result;//SetParams
    }

    public  String setNxValue(String key,Object object){
        //String value = serialize.serialize(object);
        String value = JSON.toJSONString(object);

        String result = jedis().set(key,value, SetParams.setParams().nx());
        return  result;//SetParams
    }

    public  String setNxValue(String key,String object,int millisecondsToExpire){
       // String value = JSON.toJSONString(object);

        log.info("[{}]--[{}]--[{}]",key,object,millisecondsToExpire);
        String result = jedis().set(key,object,SetParams.setParams().nx().px(millisecondsToExpire));
        return  result;//SetParams
    }

    public Object get(String key,Class clazz ){
        String value = jedis().get(key);
        Object object =  JSON.parseObject(value,clazz);
        return  object;
    }
    
    public Long ttl(String key){

        return jedis().ttl(key);
    }

    public Long expire(String key,long timeMs){
        return jedis().pexpire(key,timeMs);
    }


    public   Jedis jedis(){
       return redisPoolClient.getJedis();
    }
}

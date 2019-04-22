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
        Jedis jedis = null;
        try{
             jedis = redisPoolClient.getJedis();
            String result = jedis.set(key,object,SetParams.setParams().nx().px(millisecondsToExpire));
            return  result;
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            jedis.close();
        }
        return  null;

    }

    public String get(String key ){

        Jedis jedis = null;
        try{
            jedis = redisPoolClient.getJedis();
            String value = jedis.get(key);
            return  value;
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            jedis.close();
        }
        return  null;


    }


    public Object eval(String script ,int count ,String... params){

        Jedis jedis = null;
        try{
            jedis = redisPoolClient.getJedis();
            Object value = jedis.eval(script,count,params);
            return  value;
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            jedis.close();
        }
        return  null;


    }


    


    public   Jedis jedis(){
       return redisPoolClient.getJedis();
    }
}

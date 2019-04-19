package com.nz.rpc.lock.redis;


import com.nz.rpc.lock.LockProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LockProperties.class)
public class RedisAutoConfiguration {


   /* @Autowired
    private  LockProperties lockProperties;

    @Bean
    RedisPoolClient redisPoolClient(){

        RedisPoolClient  redisPoolClient = new RedisPoolClient(lockProperties);
        return  redisPoolClient;
    }

    @Bean
    public RedisClient redisClient(){
        RedisClient redisClient = new RedisClient();

        redisClient.setRedisPoolClient(redisPoolClient());
        return  redisClient;
    }*/


}

package com.nz.rpc.lock;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lock")
public class LockProperties {


    @Value("${lock.redis.host:127.0.0.1}")
    private String host;
    @Value("${lock.redis.port:6379}")
    private int port;

    @Value("${lock.redis.userName:root}")
    private String userName;
    @Value("${lock.redis.password:root}")
    private String password;

    @Value("${lock.redis.poolConfig.maxActive:10}")
    private int  maxActive;

    @Value("${lock.redis.poolConfig.maxActive:10}")
    private int  maxIdle;

    @Value("${lock.redis.poolConfig.maxActive:100}")
    private int  minIdle;

    @Value("${lock.redis.poolConfig.maxActive:-1}")
    private int  maxWait;


    //
    @Value("${lock.zookeeper.address:localhost:2182}")
    private String zookeeperAdress;

}

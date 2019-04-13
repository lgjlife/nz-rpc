package com.nz.rpc.zk;


import com.nz.rpc.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)
@Slf4j
public class ZookeeperAutoConfigure {


    @Autowired
    private  RpcProperties properties;

    @Bean
    public  ZkCli zkCli(){
        ZkCli zkCli = new ZkCli();
        zkCli.setProperties(properties);
        zkCli.connect();
        return  zkCli;
    }

}
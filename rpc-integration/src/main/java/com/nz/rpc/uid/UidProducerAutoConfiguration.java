package com.nz.rpc.uid;


import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.zk.ZkCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UidProducerAutoConfiguration {


    @Autowired
    private  RpcProperties properties;

    @Autowired
    private ZkCli zkCli;


    @Bean
    @ConditionalOnProperty(name="nzrpc.uid.type",havingValue = "zookeeper")
    public  UidProducer zkUidProducer(){

        log.debug("uid 生成方式:{zookeeper}");
        ZkUidProducer producer = new ZkUidProducer(zkCli);

        return producer;
    }

    @Bean
    @ConditionalOnProperty(name="nzrpc.uid.type",havingValue = "custom")
    public  UidProducer customProducer(){
        log.debug("uid 生成方式:{custom}");
        return  new CustomProducer(0);

    }



}

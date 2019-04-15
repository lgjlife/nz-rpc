package com.nz.rpc.utils.uid;


import com.nz.rpc.zk.ZkCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UidAutoConfiguration {

    @Autowired
    private  ZkCli zkCli;


    @Bean
    public  ZkUidProducer zkUidProducer(){
        ZkUidProducer producer = new ZkUidProducer(zkCli);

        return producer;
    }

    @Bean
    public  CustomProducer customProducer(){
        return  new CustomProducer();

    }
}

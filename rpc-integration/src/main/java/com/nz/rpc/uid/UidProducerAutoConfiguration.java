package com.nz.rpc.uid;


import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.zk.ZkCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public  UidProducer zkUidProducer(){

        String uidType = properties.getUid().getType();
        UidProducer producer = null;
        log.debug("uid 生成方式:{}",uidType);
        if("zookeeper".equals(uidType)){
             producer = new ZkUidProducer(zkCli);
        }
        else if("custom".equals(uidType)){
            producer = new CustomProducer(0);
        }

        return producer;
    }

}

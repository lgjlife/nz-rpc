package com.nz.rpc.msg;


import com.nz.rpc.loadbalance.RandomLoadbalanceStrategy;
import com.nz.rpc.utils.uid.CustomProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MsgAutoConfiguration {

    @Autowired
    CustomProducer customProducer;

    @Bean
    MsgRequestHandler msgRequestHandler(){

        MsgRequestHandler requestHandler =  MsgRequestHandler.getInstance();
        requestHandler.setLoadbalanceStrategy(new RandomLoadbalanceStrategy());
        requestHandler.setUidProducer(customProducer);
        return requestHandler;
    }

}

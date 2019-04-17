package com.nz.rpc.msg;


import com.nz.rpc.loadbalance.RandomLoadbalanceStrategy;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.utils.uid.CustomProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MsgAutoConfiguration {

    @Autowired
    CustomProducer customProducer;

    @Autowired
    NettyClient nettyClient;

    @Bean
    ClientMessageHandler msgRequestHandler(){

        ClientMessageHandler requestHandler =  ClientMessageHandler.getInstance();
        requestHandler.setLoadbalanceStrategy(new RandomLoadbalanceStrategy());
        requestHandler.setUidProducer(customProducer);
        requestHandler.setNettyClient(NettyContext.getNettyClient());
        return requestHandler;
    }

}

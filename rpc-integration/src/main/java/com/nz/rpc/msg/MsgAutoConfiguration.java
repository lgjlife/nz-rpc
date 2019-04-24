package com.nz.rpc.msg;


import com.nz.rpc.loadbalance.RandomLoadbalanceStrategy;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.uid.UidProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MsgAutoConfiguration {

    @Autowired(required = false)
    UidProducer uidProducer;

    @Autowired
    NettyClient nettyClient;

    @Bean
    ClientMessageHandler msgRequestHandler(){

        ClientMessageHandler requestHandler =  ClientMessageHandler.getInstance();
        requestHandler.setLoadbalanceStrategy(new RandomLoadbalanceStrategy());
        requestHandler.setUidProducer(uidProducer);
        requestHandler.setNettyClient(NettyContext.getNettyClient());
        return requestHandler;
    }

}

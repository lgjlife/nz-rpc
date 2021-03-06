package com.nz.rpc.netty.client;

import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.properties.RpcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NettyClientAutoConfiguration {

    @Autowired
    RpcProperties rpcProperties;

    @Bean
    NettyClient nettyClient(){
        NettyClient nettyClient = new NettyClient(rpcProperties);
        nettyClient.init();
        NettyContext.setNettyClient(nettyClient);
       // NettyContext.setSerialize(SerializationCreate.create(rpcProperties.getSerialization()));

        return  nettyClient;
    }

}

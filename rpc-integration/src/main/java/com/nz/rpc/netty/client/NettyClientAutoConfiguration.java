package com.nz.rpc.netty.client;

import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.serialization.SerializationCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NettyClientAutoConfiguration {

    @Autowired
    RpcProperties rpcProperties;

    @Bean
    NettyClient nettyClient(){
        NettyClient nettyClient = new NettyClient();
        if(rpcProperties.getNport() != 8121){
            nettyClient.connect("127.0.0.1",8121);
        }

        NettyContext.setNettyClient(nettyClient);
        NettyContext.setSerialize(SerializationCreate.create("jdk"));

        return  nettyClient;
    }

}

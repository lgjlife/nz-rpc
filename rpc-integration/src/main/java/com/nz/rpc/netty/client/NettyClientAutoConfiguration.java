package com.nz.rpc.netty.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NettyClientAutoConfiguration {


    @Bean
    NettyClient nettyClient(){
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect("127.0.0.1",8121);
        return  nettyClient;
    }

}

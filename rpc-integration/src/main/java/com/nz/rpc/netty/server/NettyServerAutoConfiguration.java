package com.nz.rpc.netty.server;


import com.nz.rpc.properties.RpcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyServerAutoConfiguration {

    @Autowired
    private  RpcProperties properties;

    @Bean
    public  NettyServer nettyServer(){
        NettyServer server = new NettyServer();
        server.bind(properties.getNport());

        return  server;
    }
}

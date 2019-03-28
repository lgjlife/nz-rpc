package com.nz.rpc.client.proxy;


import com.nz.rpc.client.config.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)
@Slf4j
public class RpcProxyConfiguration {

    @Autowired
    private RpcProperties properties;


    @Bean
    public  RpcProxyRegister proxyRegister(RpcProxyFactory factory){
        RpcProxyRegister proxyRegister = new RpcProxyRegister();
        proxyRegister.setProxyFactory(factory);
        log.debug("properties = " + properties);
       // proxyRegister.setProperties(properties);
        return  proxyRegister;

    }

    @Bean
    public  RpcProxyFactory proxyFactory(RpcInvoker invoker){
        RpcProxyFactory factory = new RpcProxyFactory();
        factory.setRpcInvoker(invoker);
        return  factory;
    }

    @Bean
    public RpcInvoker invoker(){
        RpcInvoker invoker = new RpcInvoker();
        return  invoker;
    }


    @PostConstruct
    public  void init(){
        log.debug("RpcProxyConfiguration init................................");
        log.debug("RpcProxyConfiguration zhost={},zport={}",properties.getZhost() , properties.getZport());
    }
}


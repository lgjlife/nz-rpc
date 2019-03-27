package com.nz.rpc.client.proxy;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcProxyConfiguration {


    @Bean
    public  RpcProxyRegister proxyRegister(RpcProxyFactory factory){
        RpcProxyRegister proxyRegister = new RpcProxyRegister();
        proxyRegister.setProxyFactory(factory);
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


}


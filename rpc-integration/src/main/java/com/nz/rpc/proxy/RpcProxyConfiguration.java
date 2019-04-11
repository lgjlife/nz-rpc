package com.nz.rpc.proxy;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/*@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)*/
@Slf4j
public class RpcProxyConfiguration {


    @Bean
    public  RpcProxyRegister rpcProxyRegister(){
        RpcProxyRegister register = new RpcProxyRegister();
        register.setProxyFactory(proxyFactory(invoker()));
        return  register;
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


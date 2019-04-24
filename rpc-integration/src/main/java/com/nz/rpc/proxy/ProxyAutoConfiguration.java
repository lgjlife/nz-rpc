package com.nz.rpc.proxy;


import com.nz.rpc.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyAutoConfiguration {

    @Autowired
    private  RpcProperties properties;

    @Bean
    public RpcProxyFactory rpcProxyFactory(){
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(properties.getProxyType());
        return rpcProxyFactory;
    }
}

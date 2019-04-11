package com.nz.rpc.proxy;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/*@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)*/
@Slf4j
public class RpcProxyConfiguration {


    @Autowired
    ApplicationContext context;



    @Bean
    public  RpcProxyRegister rpcProxyRegister(){
        RpcProxyRegister register = new RpcProxyRegister();
        register.setProxyFactory(proxyFactory(invoker()));
       // register.setContext(context);
      //  register.discoverProxy();
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


/*    @Autowired
    private RpcProperties properties;


    @Bean
    public  RpcProxyRegister proxyRegister(RpcProxyFactory factory){
        RpcProxyRegister proxyRegister = new RpcProxyRegister();
        proxyRegister.setProxyFactory(factory);
        log.debug("properties = " + properties);
       // proxyRegister.setProperties(properties);
        return  proxyRegister;

    }




    @PostConstruct
    public  void init(){
        log.debug("RpcProxyConfiguration init................................");
        log.debug("RpcProxyConfiguration zhost={},zport={}",properties.getZhost() , properties.getZport());
    }*/
}


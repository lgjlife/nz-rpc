package com.nz.rpc.client.config;

import com.nz.rpc.client.config.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)
@Slf4j
public class StarterAutoConfigure {

    @Autowired
    private RpcProperties properties;

    @Autowired
    ApplicationContext context;

    @Autowired
    WebApplicationContext webApplicationContext;



    @Bean
    public  ServiceRecovery recovery(){
        ServiceRecovery recovery = new ServiceRecovery();
        recovery.setProperties(properties);
        recovery.connect();
        recovery.recoveryService();
        return  recovery;
    }

    @PostConstruct
    public  void init(){
        log.debug("StarterAutoConfigure init................................");
        log.debug("StarterAutoConfigure zhost={},zport={}",properties.getZhost() , properties.getZport());
    }
}
package com.nz.rpc.client.config;

import com.nz.rpc.client.config.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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



    @PostConstruct
    public  void init(){
        log.debug("init................................");
        log.debug("zhost={},zport={}",properties.getZhost() , properties.getZport());
    }
}
package com.nz.rpc.rpcserver.config;

import com.nz.rpc.rpcserver.properties.RpcProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean
   // @ConditionalOnProperty(prefix = "example.service", value = "enabled", havingValue = "true")
  //  @ConditionalOnProperty(prefix = "nzrpc.server", value = "enabled", havingValue = "true")
    ZkService starterService (){
        ZkService zkService =  new ZkService(properties,context);
        log.debug(context.getDisplayName() + "  -- " + context.getApplicationName());
        zkService.connect();
        zkService.registerService();
        return zkService;
    }


    @PostConstruct
    public  void init(){
        log.debug("zhost={},zport={}",properties.getZhost() , properties.getZport());
    }
}
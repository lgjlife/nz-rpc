package com.nz.rpc.client.config.netty;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 *功能描述
 * @author lgj
 * @Description   netty 主配置
 * @date 3/16/19
*/
@Configuration
@Slf4j
public class NettyClientConfig  {




    @Bean
    public  NettyClient nettyClient(){
        NettyClient nettyClient = new NettyClient();
        return  nettyClient;
    }
    @PostConstruct
    public  void init(){
        try {
          //  this.connect("127.0.0.1", 8112);
            nettyClient().connect("127.0.0.1", 8112);
            log.debug("client connect to 127.0.0.1:8112 ");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}

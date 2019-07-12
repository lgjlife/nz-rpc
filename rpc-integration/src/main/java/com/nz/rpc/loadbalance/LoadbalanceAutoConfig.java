package com.nz.rpc.loadbalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡配置类
 * @date 7/12/19
*/
@Configuration
public class LoadbalanceAutoConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    public LoadbanlanceHandler loadbanlanceHandler(){
        LoadbanlanceHandler loadbanlanceHandler = new LoadbanlanceHandler(context);
        loadbanlanceHandler.scan();
        return loadbanlanceHandler;
    }


    //轮询
    @Bean
    LoadbalanceStrategy pollingLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new PollingLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
    //加权轮询
    @Bean
    LoadbalanceStrategy weightPollingLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new WeightPollingLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
    //随机
    @Bean
    LoadbalanceStrategy randomLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new RandomLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
    //加权随机
    @Bean
    LoadbalanceStrategy weightRandomLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new WeightRandomLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
    //一致性hash
    @Bean
    LoadbalanceStrategy uniformityHashLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new UniformityHashLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
}

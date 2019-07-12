package com.nz.rpc.loadbalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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


    @Bean
    LoadbalanceStrategy pollingLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new PollingLoadbalanceStrategy();
        return loadbalanceStrategy;
    }

    @Bean
    LoadbalanceStrategy weightPollingLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new WeightPollingLoadbalanceStrategy();
        return loadbalanceStrategy;
    }

    @Bean
    LoadbalanceStrategy randomLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new RandomLoadbalanceStrategy();
        return loadbalanceStrategy;
    }

    @Bean
    LoadbalanceStrategy weightRandomLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new WeightRandomLoadbalanceStrategy();
        return loadbalanceStrategy;
    }

    @Bean
    LoadbalanceStrategy uniformityHashLoadbalanceStrategy(){
        LoadbalanceStrategy loadbalanceStrategy = new UniformityHashLoadbalanceStrategy();
        return loadbalanceStrategy;
    }
}

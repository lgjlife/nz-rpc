package com.nz.rpc.cluster;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错配置类
 * @date 7/12/19
*/
@Configuration
public class ClusterFaultAutoconfiguration {

    @Autowired
    private ApplicationContext context;

    @Bean
    public ClusterFaultHandler clusterFaultHandler(){
        ClusterFaultHandler clusterFaultHandler = new ClusterFaultHandler();
        clusterFaultHandler.setContext(context);
        clusterFaultHandler.scan();
        return clusterFaultHandler;
    }

    @Bean
    public ClusterFault failfastClusterFaultTolerance(){
       return new FailfastClusterFault();
    }

    @Bean
    public FailsaveClusterFault failsaveClusterFault(){
        return new FailsaveClusterFault();
    }

    @Bean
    FailbackClusterFault failbackClusterFault(){
        return new FailbackClusterFault();
    }
}

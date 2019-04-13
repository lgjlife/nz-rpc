package com.nz.rpc.discover;


import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.provider.ProviderHandle;
import com.nz.rpc.zk.ZkCli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@ConditionalOnClass(RpcProperties.class)
@EnableConfigurationProperties(RpcProperties.class)
@Configuration
public class DiscoverAutoConfiguration {

    @Autowired
    ApplicationContext context;


    @Autowired
    RpcProperties rpcProperties;

    @Autowired
    ProviderHandle providerHandle;

    @Autowired
    private  ZkCli zkCli;



    @Bean
    public  ZookeeperServiceRegister zookeeperServiceRegister(){
        ZookeeperServiceRegister register  = new ZookeeperServiceRegister();
        register.setZkCli(zkCli);
        register.setContext(context);
        register.setProviderHandle(providerHandle);
        register.setProperties(rpcProperties);

        register.test();
        register.providerDiscover();
        register.registerService();
        register.consumerDiscover();

        return register;
    }

}

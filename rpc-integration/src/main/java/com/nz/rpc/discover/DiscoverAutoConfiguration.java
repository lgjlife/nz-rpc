package com.nz.rpc.discover;


import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.proxy.RpcProxyFactory;
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
    private  ZkCli zkCli;

    @Autowired
    public  RpcProxyFactory rpcProxyFactory;


    @Autowired
    private NettyClient nettyClient;




    /**
     *功能描述
     * @author lgj
     * @Description  注册服务提供者
     * @date 4/13/19
     * @param:
     * @return:
     *
    */
    @Bean
    public  ZookeeperServiceRegister zookeeperServiceRegister(){
        ZookeeperServiceRegister register  = new ZookeeperServiceRegister();
        register.setZkCli(zkCli);
        register.setContext(context);
        register.setProperties(rpcProperties);
        register.setRpcProxyFactory(rpcProxyFactory);
        //向zookeeper注册被 {@link com.nz.rpc.anno.RpcService}注解的类
        register.registerService();
        //查找被@RpcReference注解的消费者接口引用，并注入bean容器
        register.consumerDiscover();

        return register;
    }

    /**
    *功能描述
    * @author lgj
    * @Description
    * @date 4/13/19
    * @param:
    * @return:
    *
   */
    @Bean
    public  ZookeeperServiceDiscover zookeeperServiceDiscover(){
        ZookeeperServiceDiscover discover  = new ZookeeperServiceDiscover(zkCli);
        discover.setZkCli(zkCli);
        discover.setContext(context);
        discover.setProperties(rpcProperties);
        discover.setNettyClient(nettyClient);
        discover.queryService();


        return discover;
    }



}

package com.nz.rpc.loadbalance;

import com.nz.rpc.constans.RpcClientConstans;
import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.discover.ProviderConfigContainer;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.loadbalance.exception.LoadbalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *功能描述 
 * @author lgj
 * @Description   负载均衡处理
 * @date 7/12/19
*/
@Slf4j
public class LoadbanlanceHandler {

    private ApplicationContext context;
    //随机负载均衡策略类
    private final Class defaultLoadbalanceStrategy = PollingLoadbalanceStrategy.class;

    private Map<String,Class<? extends LoadbalanceStrategy>> serviceLoadbanlanceStrategy = new ConcurrentHashMap<>();


    public LoadbanlanceHandler(ApplicationContext context) {
        this.context = context;
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  扫描负载均衡的配置类，确定接口对应的负载均衡策略
     * @date 7/12/19
     * @param: 
     * @return:  
     *
    */
    public void scan(){

        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        Map<String, AbstractLoadbalanceConfig> loadbalanceConfigs = listableBeanFactory.getBeansOfType(AbstractLoadbalanceConfig.class);

        loadbalanceConfigs.forEach((key,value)->{

            Map<String,Class<? extends LoadbalanceStrategy>> configs = value.config();
            configs.forEach((k,v)->{
                serviceLoadbanlanceStrategy.put(k,v);
            });
        });

        log.debug("serviceLoadbanlanceStrategy = "+serviceLoadbanlanceStrategy);
    }

    private LoadbalanceStrategy getLoadbalanceStrategy(String interfaceName){
            Class strategyClass = serviceLoadbanlanceStrategy.get(interfaceName);

            //用户没有配置，获取默认
            if(strategyClass==null){
                return (LoadbalanceStrategy)context.getBean(defaultLoadbalanceStrategy);
            }
            LoadbalanceStrategy  loadbalanceStrategy = (LoadbalanceStrategy)context.getBean(strategyClass);
            if(loadbalanceStrategy == null){
                return (LoadbalanceStrategy)context.getBean(defaultLoadbalanceStrategy);
            }
            return loadbalanceStrategy;
    }

    /**
     *功能描述
     * @author lgj
     * @Description 负载均衡选择Server
     * @date 7/12/19
     * @param:
     * @return:
     *
     */
    public void serviceSelect(ClientInvocation invocation) throws Exception{

        //获取消费者
        Map<String, ProviderConfig> configMap = ProviderConfigContainer.getConfigMap();

        List<ProviderConfig> registryConfigLists = new ArrayList<>();
        configMap.forEach((k,v)->{
            if(v.getInterfaceName().equals(invocation.getMethod().getDeclaringClass().getName())){
                registryConfigLists.add(v);
            }
        });
        //
        log.debug("The list of :{}",registryConfigLists);

        //负载均衡处理

        LoadbalanceStrategy loadbalanceStrategy = null;
        if((registryConfigLists == null) || (registryConfigLists.isEmpty())){
            throw new LoadbalanceException("Loadbalance fail!,No provider can select! ");
        }
        String serviceInterfaceName = registryConfigLists.get(0).getInterfaceName();
        loadbalanceStrategy = getLoadbalanceStrategy(serviceInterfaceName);
        log.debug("loadbalanceStrategy is [{}]",loadbalanceStrategy.getClass().getName());
        ProviderConfig registryConfig =  loadbalanceStrategy.select(registryConfigLists,null);
        log.debug("The loadbalance server is [{}] ",registryConfig);
        invocation.getAttachments().put(RpcClientConstans.NETTY_REQUEST_HOST,registryConfig.getHost());
        invocation.getAttachments().put(RpcClientConstans.NETTY_REQUEST_PORT,registryConfig.getPort().toString());
    }
}

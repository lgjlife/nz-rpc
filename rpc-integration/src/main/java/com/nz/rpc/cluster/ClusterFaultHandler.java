package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClusterFaultHandler {

    private ApplicationContext context;

    private Class defaultClusterFaultType = FailfastClusterFault.class;

    private Map<String,Class<? extends ClusterFault>> clusterMap = new ConcurrentHashMap<>();

    /**
     *功能描述
     * @author lgj
     * @Description  扫描集群容错的配置
     * @date 7/12/19
     * @param:
     * @return:
     *
    */
    public void scan(){
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        Map<String,AbstractClusterFaultConfig> clusterFaultConfigMap = listableBeanFactory.getBeansOfType(AbstractClusterFaultConfig.class);

        clusterFaultConfigMap.forEach((name,configs)->{
            Map<String,Class<? extends ClusterFault>>  clusterFaultToleranceMap = configs.config();

            clusterFaultToleranceMap.forEach((serviceName,clusterFaultClass)->{
                clusterMap.put(serviceName,clusterFaultClass);
            });
        });

        log.debug("clusterMap = " +clusterMap);
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  根据接口名称获取集群容错实现类
     * @date 7/12/19
     * @param: 
     * @return:  
     *
    */
    private ClusterFault getClusterFault(String serviceName){
        Class clusterFaultType =   clusterMap.get(serviceName);

        if(clusterFaultType == null){
            clusterFaultType = defaultClusterFaultType;
        }
        ClusterFault clusterFault =  (ClusterFault)context.getBean(clusterFaultType);
        return clusterFault;
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  集群容错处理
     * @date 7/12/19
     * @param: 
     * @return:  
     *
    */
    public Object  handle(ClientInvocation invocation,Exception ex) throws Exception{
        //获取接口对应的容错处理对象
        ClusterFault clusterFault =  getClusterFault(invocation.getMethod().getDeclaringClass().getName());
        Object result = clusterFault.handle(invocation,ex);
        return result;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }
}

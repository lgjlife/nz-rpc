package com.nz.rpc.rpcserver.config;

import com.nz.rpc.rpcserver.properties.RpcProperties;
import com.nz.rpc.rpcsupport.annotation.RpcService;
import com.nz.rpc.rpcsupport.utils.RegistryConfig;
import com.nz.rpc.rpcsupport.utils.ZookeeperPath;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.HessianSerializeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *功能描述 
 * @author lgj
 * @Description  zookeeper  注册服务
 * /nzRpc/className/providers
 * @date 3/27/19
*/

@Slf4j
@Data
public class ZkRegisterService {

    private CuratorFramework client;
    private RpcProperties properties;

    private AbstractSerialize serialize = HessianSerializeUtil.getSingleton();
    //应用上下文,用于获取注解
    ApplicationContext context;


    public ZkRegisterService(RpcProperties properties, ApplicationContext context){
        this.properties = properties;
        this.context = context;
    }

    /**
     *功能描述
     * @author lgj
     * @Description  连接到zookeeper
     * @date 3/27/19
     * @param:
     * @return:
     *
    */
    public void connect() {
        //拒绝策略
        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(properties.getZhost()+":"+properties.getZport(),
                retryPolicy);
        client.start();
        log.debug("zookeeper client start....");
      //  setListener(client);
    }
    
    /**
     *功能描述 
     * @author lgj
     * @Description   向zookeeper注册服务
     * @date 3/27/19
     * @param: 
     * @return: 
     *
    */
    public void registerService() throws BeansException {
        log.debug("ServiceRegistry setApplicationContext..");

        Map<String, Object> annotations = context.getBeansWithAnnotation(RpcService.class);

        if (!CollectionUtils.isEmpty(annotations)) {
            annotations.values().forEach((service) -> {
                String serviceClass = service.getClass()
                        .getAnnotation(RpcService.class).interfaceClass().getName();
                if (serviceClass == "void") {
                    serviceClass = service.getClass().getName();
                }
                log.debug("注册接口 {}", serviceClass);
                // createRootPath(serviceClass);
                registryConfig(serviceClass, context.getId());
            });
        }

    }

    /**
     * 功能描述
     *
     * @author lgj
     * @Description 注册应用信息
     * @date 1/26/19
     * @param:
     * @return:
     */
    private void registryConfig(String serviceClass, String appName) {

        String regPath = getPath(serviceClass);
        try {
            if (client.checkExists().forPath(regPath) == null) {
                log.debug("目录{}不存在，创建目录....", regPath);
                client.create().creatingParentsIfNeeded().forPath(regPath);
            } else {
                log.debug("目录{}已经存在,获取目录信息", regPath);
            }
            //   client.transaction();
            byte[] data = client.getData().forPath(regPath);
            log.info("data len  = " + data.length);
            List<RegistryConfig> configs = null;
            if (data.length > 9) {
                configs = (List) serialize.deserialize(data,ArrayList.class);
                log.debug("读取的信息:size = {}, configs = {} ", configs.size(), configs);
                configs.removeIf((config) -> config.getApplication().equals(appName));

            } else {
                log.debug("读取的信息 configs = 未存在");
                configs = new ArrayList<>();
            }

            //  configs = new ArrayList<>();

            //获取注册信息
            RegistryConfig config = new RegistryConfig();
            config.setHost(properties.getZhost());
            config.setPort(properties.getNport());
            config.setApplication(appName);
            config.setInterfaceName(serviceClass);
            Class clz = Class.forName(serviceClass);
            Method[] methods = clz.getDeclaredMethods();
            String[] methodNames = new String[methods.length];
            for (int i = 0; i < methods.length; i++) {
                methodNames[i] = methods[i].getName();
            }
            config.setMethods(methodNames);
            configs.add(config);

            log.debug("注册信息 configs = " + configs);
            byte[] regData = serialize.serialize(configs);
            log.info("regData len = " + regData.length);
            //注册
            log.info("regPath = " + regPath);
            client.setData().forPath(regPath, regData);

            //client.setData().forPath(createPath,);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 功能描述
     *
     * @author lgj
     * @Description 创建根目录
     * @date 1/26/19
     * @param:
     * @return:
     */
    private void createRootPath(String path) {

        String createPath = getPath(path);
        try {
            if (client.checkExists().forPath(createPath) == null) {
                log.debug("目录{}不存在，创建目录....", createPath);
                client.create().creatingParentsIfNeeded().forPath(createPath);
            } else {
                log.debug("目录{}已经存在", createPath);
            }

            //client.setData().forPath(createPath,);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private String getPath(String serviceClass) {
        return ZookeeperPath.rootPath + "/" + serviceClass + ZookeeperPath.providersPath;
    }


    public  void setListener(CuratorFramework client ){
        try{
            TreeCache treeCache = new TreeCache(client,"/app");
            treeCache.getListenable().addListener(new ZkListener());
            treeCache.start();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

}

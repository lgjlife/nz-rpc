package com.nz.rpc.client.config;


import com.nz.rpc.client.config.properties.RpcProperties;
import com.nz.rpc.client.utils.ZooKeeperConfig;
import com.nz.rpc.rpcsupport.utils.RegistryConfig;
import com.nz.rpc.rpcsupport.utils.ZookeeperPath;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.HessianSerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *功能描述 
 * @author lgj
 * @Description 
 * @date 3/28/19
*/

@Slf4j
public class ServiceRecovery  {

    private CuratorFramework client;

    private AbstractSerialize serialize = HessianSerializeUtil.getSingleton();


    private RpcProperties properties;


    public void setProperties(RpcProperties properties) {
        this.properties = properties;
    }

    /**
     * 功能描述
     *
     * @author lgj
     * @Description 创建客户端client
     * @date 1/26/19
     * @param:
     * @return:
     */
    public void connect() {
        //拒绝策略
        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(properties.getAddress(),
                retryPolicy);
        client.start();

        try{
            final TreeCache cached = new TreeCache(client,ZookeeperPath.rootPath);
            cached.getListenable().addListener(new TreeCacheListener(){
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    switch (treeCacheEvent.getType()) {

                        case NODE_ADDED:
                            ;
                        case NODE_REMOVED:
                            ;
                        case NODE_UPDATED:
                            recoveryService();
                            break;

                    }
                }
            });
            cached.start();
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

    }

    public void recoveryService() {

        try {
            List<String> childPath = client.getChildren().forPath(ZookeeperPath.rootPath);
            childPath.forEach((path) -> log.debug(path));

            for (String classPath : childPath) {
                String regPath = getPath(classPath);
                byte[] data = client.getData().forPath(regPath);
                log.info("data len  = " + data.length);
                List<RegistryConfig> configs = null;
                if (data.length > 9) {
                    configs = (List) serialize.deserialize(data,ArrayList.class);
                    log.debug("读取的信息:size = {}, configs = {} ", configs.size(), configs);
                    ServiceConfig.setConfigs(configs);
                }

            }


        } catch (Exception ex) {
            log.error(ex.getMessage());
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

   // @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.debug("ServiceRegistry setApplicationContext..");

        connect();
        recoveryService();
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
            byte[] data = client.getData().usingWatcher(new CuratorWatcher() {
                @Override
                public void process(WatchedEvent watchedEvent) throws Exception {
                    System.out.println("watchedEvent = " + watchedEvent);

                }
            }).forPath(regPath);
            log.info("data len  = " + data.length);
            List<RegistryConfig> configs = null;
            if (data.length > 9) {
                configs = (List) serialize.deserialize(data,ArrayList.class);
                //  configs.removeIf((config)->config.getApplication().equals(appName));
                log.debug("读取的信息 configs = " + configs);
            } else {
                log.debug("读取的信息 configs = 未存在");
                configs = new ArrayList<>();
            }

            //  configs = new ArrayList<>();

            //获取注册信息
            RegistryConfig config = new RegistryConfig();
            config.setHost(ZooKeeperConfig.HOST);
            config.setPort(ZooKeeperConfig.PORT);
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
            log.info("path = " + regPath);
            client.setData().forPath(regPath, regData);

            //client.setData().forPath(createPath,);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

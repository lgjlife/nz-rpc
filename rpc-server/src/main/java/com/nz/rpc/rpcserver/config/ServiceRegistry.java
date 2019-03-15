package com.nz.rpc.rpcserver.config;


import com.nz.rpc.rpcserver.config.zookeeper.ZookeeperConfig;
import com.nz.rpc.rpcserver.utils.ZooKeeperConfig;
import com.nz.rpc.rpcsupport.annotation.RpcService;
import com.nz.rpc.rpcsupport.serialize.JdkSerialize;
import com.nz.rpc.rpcsupport.serialize.Serialize;
import com.nz.rpc.rpcsupport.utils.RegistryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.ContextIdApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@ConditionalOnBean(value = com.nz.rpc.rpcserver.config.zookeeper.ZookeeperConfig.class)
@Component
@Slf4j
public class ServiceRegistry implements ApplicationContextAware {

    private CuratorFramework client;
    //path =  rootPath/class name / providersPath
    private String rootPath = "/nzRpc";
    private String providersPath = "/providers";
    private Serialize serialize = new JdkSerialize();
    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Value("${spring.application.name}")
    private String appName;


    ContextIdApplicationContextInitializer springApp = new ContextIdApplicationContextInitializer();

    /**
     * 功能描述
     *
     * @author lgj
     * @Description 创建客户端client
     * @date 1/26/19
     * @param:
     * @return:
     */
    private void connect() {
        //拒绝策略
        RetryPolicy retryPolicy
                = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperConfig.address(),
                retryPolicy);
        client.start();
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
        return rootPath + "/" + serviceClass + providersPath;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.debug("ServiceRegistry setApplicationContext..");
        //log.info("context = " + context);

        log.debug("host = " + zookeeperConfig.getHost());
        log.debug("port = " + zookeeperConfig.getPort());

        connect();

        //   String appName = context.getApplicationName();


        log.debug("ApplicationName = " + appName);

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
                registryConfig(serviceClass, appName);
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
            byte[] data = client.getData().usingWatcher(new CuratorWatcher() {
                @Override
                public void process(WatchedEvent watchedEvent) throws Exception {
                    // System.out.println("watchedEvent = " + watchedEvent );

                }
            }).forPath(regPath);
            log.info("data len  = " + data.length);
            List<RegistryConfig> configs = null;
            if (data.length > 9) {
                configs = (List) serialize.deserialize(data);
                log.debug("读取的信息:size = {}, configs = {} ", configs.size(), configs);
                configs.removeIf((config) -> config.getApplication().equals(appName));

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

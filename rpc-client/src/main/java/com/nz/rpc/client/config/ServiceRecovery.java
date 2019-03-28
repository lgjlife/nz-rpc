package com.nz.rpc.client.config;


import com.nz.rpc.client.config.properties.RpcProperties;
import com.nz.rpc.rpcsupport.utils.RegistryConfig;
import com.nz.rpc.rpcsupport.utils.ZookeeperPath;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.HessianSerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

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

        //配置监听器
        try{
            final TreeCache cached = new TreeCache(client,ZookeeperPath.rootPath);
            cached.getListenable().addListener(new TreeCacheListener(){
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    switch (treeCacheEvent.getType()) {

                        case NODE_ADDED:
                            log.debug("zookeeper NODE_ADDED" );
                            ;
                        case NODE_REMOVED:
                            log.debug("zookeeper NODE_REMOVED" );
                            ;
                        case NODE_UPDATED:
                            log.debug("zookeeper NODE_UPDATED" );
                            String  path =treeCacheEvent.getData().getPath();
                            log.debug("NODE_UPDATED  path = " + path);
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

        List<RegistryConfig> configs = new ArrayList<>();

        log.debug("recoveryService............");
        try {
            List<String> childPath = client.getChildren().forPath(ZookeeperPath.rootPath);


            childPath.forEach((classPath) -> {
                log.info("读取类{}信息",classPath);

                String regPath = getPath(classPath);
                try{
                    byte[] data = client.getData().forPath(regPath);
                  //  log.info("读取类{}信息",classPath);

                    if (data.length > 9) {
                        List<RegistryConfig> cfg = (List) serialize.deserialize(data,ArrayList.class);
                        log.debug("读取的信息:size = {}, configs = {} ", cfg.size(), cfg);
                        //String key = configs
                      //  configs.addAll(cfg);
                        ServiceConfig.updateServices(classPath,cfg);
                    }
                }
                catch(Exception ex){
                    log.error(ex.getMessage());
                }

            });

          //  ServiceConfig.setConfigs(configs);

            log.debug("configs = {}",ServiceConfig.getServices());


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


}

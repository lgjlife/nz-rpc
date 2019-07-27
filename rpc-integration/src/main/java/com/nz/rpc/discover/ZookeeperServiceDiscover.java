package com.nz.rpc.discover;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.zk.ListenerEventHandler;
import com.nz.rpc.zk.ZkCli;
import com.nz.rpc.zk.ZookeeperPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;
import java.util.Map;


@Slf4j
public class ZookeeperServiceDiscover extends  AbstractServiceDiscover{


    private NettyClient nettyClient;

    public ZookeeperServiceDiscover(ZkCli zkCli) {
        super(zkCli);
        //添加监听器
        zkCli.setListener(new ListenerEventHandlerImpl(),ZookeeperPath.rootPath);

    }

    @Override
    public void registerService() {
        throw  new UnsupportedOperationException();
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  从zookzeeper中获取服务信息
     * @date 7/13/19
     * @param: 
     * @return:  
     *
    */
    @Override
    public void queryService() {

        if(log.isDebugEnabled()){

        }
        log.debug("Query the server info from the zookeeper!");
        List<String> interfacePaths =  zkCli.getChildren(ZookeeperPath.rootPath);
        for(String interfacePath:interfacePaths){
            List<String> serverPaths  =  zkCli.getChildren(ZookeeperPath.rootPath
                    +  ZookeeperPath.separator
                    +  interfacePath
                    +  ZookeeperPath.providersPath);

            serverPaths.forEach(serverPath->{
                List<String> providersConfigs = zkCli.getChildren(ZookeeperPath.rootPath
                        +  ZookeeperPath.separator
                        +  interfacePath
                        +  ZookeeperPath.providersPath
                        +  ZookeeperPath.separator
                        +  serverPath);


                for(String providersConfig:providersConfigs){
                    ProviderConfig registryConfig = JSON.parseObject(providersConfig, ProviderConfig.class);
                    if(log.isDebugEnabled()){
                        log.debug("The server registryConfig = " + registryConfig );
                    }

                    ProviderConfigContainer.putConfig(registryConfig);
                }
            });
        }
        if(log.isDebugEnabled()){
            log.info("ConfigMap = "+ProviderConfigContainer.getConfigMap());
        }

        connectServer(ProviderConfigContainer.getConfigMap());
    }

    /**
     *功能描述 
     * @author lgj
     * @Description  从zk中获取到服务信息后，连接到服务server
     * @date 7/13/19
    */
    private void connectServer(Map<String, ProviderConfig> configMap){

        if(configMap == null){
            log.info("configMap = null");
        }


        configMap.forEach((key,config)->{

            if(log.isDebugEnabled()){
                log.info("config = " + config);
            }

            if(config == null){
                log.debug("config = null");
            }
            else {
                nettyClient.connect(config.getHost(),config.getPort());
            }

        });
    }


    class ListenerEventHandlerImpl implements ListenerEventHandler {

        @Override
        public void addHandler(ChildData data) {
            if(log.isDebugEnabled()){
                log.debug("ListenerEventHandlerImpl　addHandler");
            }
            queryService();
        }

        @Override
        public void removeHandler(ChildData data) {
            if(log.isDebugEnabled()){
                log.debug("ListenerEventHandlerImpl　removeHandler");
            }
            String removePath = data.getPath();
            if(log.isDebugEnabled()){
                log.debug("removePath = " + removePath);
            }

            queryService();
        }

        @Override
        public void updateHandler(ChildData data) {
            if(log.isDebugEnabled()){
                log.debug("ListenerEventHandlerImpl　updateHandler");

            }

            queryService();
        }
    }


    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }
}

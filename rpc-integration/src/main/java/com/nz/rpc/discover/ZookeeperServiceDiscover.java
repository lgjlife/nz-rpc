package com.nz.rpc.discover;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.zk.ListenerEventHandler;
import com.nz.rpc.zk.ZkCli;
import com.nz.rpc.zk.ZookeeperPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;


@Slf4j
public class ZookeeperServiceDiscover extends  AbstractServiceDiscover{

    public ZookeeperServiceDiscover(ZkCli zkCli) {
        super(zkCli);
        zkCli.setListener(new ListenerEventHandlerImpl(),ZookeeperPath.rootPath);

    }

    @Override
    public void registerService() {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void queryService() {

        List<String> interfacePaths =  zkCli.getChildren(ZookeeperPath.rootPath);
        for(String interfacePath:interfacePaths){
            List<String> providersConfigs  =  zkCli.getChildren(ZookeeperPath.rootPath
                    +  ZookeeperPath.separator
                    +  interfacePath
                    +  ZookeeperPath.providersPath);

            for(String providersConfig:providersConfigs){
                ProviderConfig registryConfig = JSON.parseObject(providersConfig, ProviderConfig.class);
                log.debug("服务提供者信息　registryConfig = " + registryConfig );
                ProviderConfigContainer.putConfig(registryConfig);
            }
        }


    }

    class ListenerEventHandlerImpl implements ListenerEventHandler {

        @Override
        public void addHandler(ChildData data) {

            log.debug("ListenerEventHandlerImpl　addHandler");
            queryService();
        }

        @Override
        public void removeHandler(ChildData data) {
            log.debug("ListenerEventHandlerImpl　removeHandler");

            String removePath = data.getPath();
            log.debug("removePath = " + removePath);
            queryService();
        }

        @Override
        public void updateHandler(ChildData data) {
            log.debug("ListenerEventHandlerImpl　updateHandler");

            queryService();
        }
    }
}

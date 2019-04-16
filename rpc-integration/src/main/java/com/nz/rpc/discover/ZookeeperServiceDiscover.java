package com.nz.rpc.discover;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.zk.ZookeeperPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;


@Slf4j
public class ZookeeperServiceDiscover extends  AbstractServiceDiscover{


    private ApplicationContext context;


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
                RegistryConfig registryConfig = JSON.parseObject(providersConfig, RegistryConfig.class);
                log.debug("服务提供者信息　registryConfig = " + registryConfig );
                RegisterConfigContainer.putConfig(registryConfig);
            }
        }


    }
}

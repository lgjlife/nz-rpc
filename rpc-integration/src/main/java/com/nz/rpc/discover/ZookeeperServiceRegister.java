package com.nz.rpc.discover;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.zk.ZkCreateConfig;
import com.nz.rpc.zk.ZookeeperPath;
import com.utils.serialization.AbstractSerialize;
import com.utils.serialization.FastjsonSerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;


/**
 *功能描述
 * @author lgj
 * @Description 注册服务服务提供者信息
 * @date 4/13/19
*/
@Slf4j
public class ZookeeperServiceRegister extends  AbstractServiceDiscover {

    @Override
    public void queryService() {
        throw  new UnsupportedOperationException();
    }
    private AbstractSerialize serialize = FastjsonSerializeUtil.getSingleton();
    /**
     *功能描述
     * @author lgj
     * @Description   向zookeeper注册被 {@link com.nz.rpc.anno.RpcService}注解的类
     * @date 3/27/19
     * @param:
     * @return:
     *　
     */
    @Override
    public void registerService() throws BeansException {

        log.debug("ServiceRegistry setApplicationContext..");
        this.providerDiscover();

        if(NettyContext.getLocalServiceImplMap() != null){
            NettyContext.getLocalServiceImplMap().forEach((k,v)->{
                log.debug("注册接口 {}", k);
                registerConfig(k, context.getId());
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
    private void registerConfig(String serviceClass, String appName) {

        String regPath = getPath(serviceClass);

        try {

            //获取注册信息
            RegistryConfig config = new RegistryConfig();

            config.setHost(properties.getNhost());
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

            String configStr = JSON.toJSONString(config);
            log.debug("configStr = {}",configStr);

            ZkCreateConfig zkCreateConfig = ZkCreateConfig
                    .builder()
                    .path(regPath + "/" + configStr)
                    .createMode(CreateMode.EPHEMERAL)
                    .build();
            zkCli.createPath(zkCreateConfig);

        } catch (Exception ex) {
           log.error("注册服务失败 {}",ex);
        }
    }

    private String getPath(String serviceClass) {
        return ZookeeperPath.rootPath + "/" + serviceClass + ZookeeperPath.providersPath;
    }


}

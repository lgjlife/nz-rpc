package com.nz.rpc.discover;

import com.alibaba.fastjson.JSON;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.zk.ZkCreateConfig;
import com.nz.rpc.zk.ZookeeperPath;
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


    public ZookeeperServiceRegister() {
        addShutdownHook();
    }

    @Override
    public void queryService() {
        throw  new UnsupportedOperationException();
    }

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
        if(log.isDebugEnabled()){
            log.debug("ServiceRegistry setApplicationContext..");
        }

        this.providerDiscover();

        if(NettyContext.getLocalServiceImplMap() != null){
            NettyContext.getLocalServiceImplMap().forEach((interfaceName,implNames)->{
                if(log.isDebugEnabled()){
                    log.debug("注册接口 {}", interfaceName);
                }

                implNames.forEach((implName)->{
                    registerConfig1(interfaceName,implName);
                });

            });
        }
    }
    /**
     * 功能描述
     *
     * @author lgj
     * @Description 注册服务应用信息
     * @date 1/26/19
     * @param:
     * @return:
     */
    private void registerConfig1(String interfaceName,String implName) {

        String regPath = getPath(interfaceName);
        regPath = regPath + "/" + properties.getNhost()+":"+properties.getNport();
        try {

            //获取注册信息
            ProviderConfig config = new ProviderConfig();

            config.setHost(properties.getNhost());
            config.setPort(properties.getNport());
            config.setApplication(context.getId());
            config.setImplName(implName);
            config.setInterfaceName(interfaceName);
            Class clz = Class.forName(interfaceName);
            Method[] methods = clz.getDeclaredMethods();
            String[] methodNames = new String[methods.length];
            for (int i = 0; i < methods.length; i++) {
                methodNames[i] = methods[i].getName();
            }
            config.setMethods(methodNames);

            String configStr = JSON.toJSONString(config);
            if(log.isDebugEnabled()){
                log.debug("configStr = {}",configStr);
            }


            ZkCreateConfig zkCreateConfig = ZkCreateConfig
                    .builder()
                    .path(regPath + "/" + configStr)
                    .createMode(CreateMode.EPHEMERAL)
                    .build();
            zkCli.createPath(zkCreateConfig);

        } catch (Exception ex) {
            if(log.isErrorEnabled()){
                log.error("注册服务失败 {}",ex);
            }

        }
    }

    private String getPath(String serviceClass) {
        return ZookeeperPath.rootPath + "/" + serviceClass + ZookeeperPath.providersPath;
    }

    /**
     *功能描述
     * @author lgj
     * @Description  添加应用关闭时的回调函数，用于删除zk节点
     * @date 7/14/19
     * @param:
     * @return:
     *
    */
    private void addShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                NettyContext.getLocalServiceImplMap().forEach((interfaceName,implNames)->{
                    String deletePath = getPath(interfaceName);
                    deletePath = deletePath + "/" + properties.getNhost()+":"+properties.getNport();

                    if(zkCli.checkExists(deletePath)){
                        if(log.isDebugEnabled()){
                            log.debug("deletePath[{}] exists",deletePath);
                        }

                        zkCli.deleteNodeAndChildren(deletePath);
                        return;
                    }
                    if(log.isWarnEnabled()){
                        log.warn("deletePath[{}] not exists",deletePath);
                    }


                });
            }
        });
    }

}

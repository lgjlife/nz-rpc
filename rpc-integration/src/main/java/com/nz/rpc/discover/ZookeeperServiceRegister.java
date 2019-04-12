package com.nz.rpc.discover;

import com.nz.rpc.utils.RegistryConfig;
import com.nz.rpc.utils.ZookeeperPath;
import com.nz.rpc.zk.ZkCli;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *功能描述
 * @author lgj
 * @Description 注册服务服务提供者信息
 * @date 4/13/19
*/
@Slf4j
public class ZookeeperServiceRegister extends  AbstractServiceDiscover{


    private  ZkCli zkCli;
    private ApplicationContext context;

    @Override
    public void queryService() {
        throw  new UnsupportedOperationException();
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
    @Override
    public void registerService() throws BeansException {

        log.debug("ServiceRegistry setApplicationContext..");

        Map<String, String> clzs = providerHandle.getClzMap();
        if(clzs != null){
            clzs.forEach((k,v)->{
                log.debug("注册接口 {}", k);
                // createRootPath(serviceClass);
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
            zkCli.createPath(regPath);

            List<RegistryConfig> configs = null;
            configs = (ArrayList)zkCli.getData(regPath,ArrayList.class);
            log.debug("读取的信息:size = {}, configs = {} ", configs.size(), configs);
            configs.removeIf((config) -> config.getApplication().equals(appName));

            //  configs = new ArrayList<>();

            //获取注册信息
            RegistryConfig config = new RegistryConfig();
            //  config.setHost(properties.getZhost());
            //  config.setPort(properties.getNport());
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
            zkCli.setData(regPath,configs);
        } catch (Exception ex) {
           log.debug("");
        }
    }

    private String getPath(String serviceClass) {
        return ZookeeperPath.rootPath + "/" + serviceClass + ZookeeperPath.providersPath;
    }

}

package com.nz.rpc.proxy;


import lombok.extern.slf4j.Slf4j;

/**
 *功能描述
 * @author lgj
 * @Description 动态代理选择
 * @date 4/14/19
*/
@Slf4j
public class ProxySelector {

    public static ProxyCreate select(String type){
        log.debug("ProxySelector 动态代理：{}",type);
        switch (type){
            case "jdk": return  new JdkProxyCreate();
            case "cglib": return  new CglibProxyCreate();
            case "javassit": return  new JavassitProxyCreate();
            default: return  new JdkProxyCreate();
        }
    }
}

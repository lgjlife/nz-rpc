package com.nz.rpc.proxy;


/**
 *功能描述
 * @author lgj
 * @Description 动态代理选择
 * @date 4/14/19
*/
public class ProxySelector {

    public static ProxyCreate select(String type){
        switch (type){
            case "jdk": return  new JdkProxyCreate();
            case "cglib": return  new JavassitProxyCreate();
            case "javassit": return  new CglibProxyCreate();
            default: return  new JdkProxyCreate();
        }
    }
}

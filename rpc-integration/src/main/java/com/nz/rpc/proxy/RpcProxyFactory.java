package com.nz.rpc.proxy;

import lombok.extern.slf4j.Slf4j;


/**
 *功能描述
 * @author lgj
 * @Description  动态代理工厂
 * @date 4/14/19
*/
@Slf4j
public class RpcProxyFactory {

    private RpcInvoker rpcInvoker = new RpcInvoker();
    private ProxyCreate proxyCreate;

    public  RpcProxyFactory(String type){
        this.proxyCreate = ProxySelector.select(type).invoker(rpcInvoker);
    }
    public <T> T createInstance(Class<T> interfaceClass) {
        return proxyCreate.createInstance(interfaceClass);
    }
}

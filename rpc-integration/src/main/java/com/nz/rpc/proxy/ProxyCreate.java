package com.nz.rpc.proxy;


/**
 *功能描述
 * @author lgj
 * @Description  动态代理创建类接口
 * @date 4/14/19
*/
public interface ProxyCreate {
    public <T> T createInstance(Class<T> interfaceClass);

    public ProxyCreate invoker(RpcInvoker invoker);
}
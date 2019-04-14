package com.nz.rpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 *功能描述
 * @author lgj
 * @Description  jdk
 * @date 4/14/19
*/
@Slf4j
public class JdkProxyCreate implements ProxyCreate {

    private  RpcInvoker rpcInvoker;
    public <T> T createInstance(Class<T> interfaceClass){
        log.info("use jdk dynamic proxy : " + interfaceClass.getSimpleName());
        log.debug("getClassLoader = " + interfaceClass.getClassLoader()
                + " getInterfaces  =   " + interfaceClass.getInterfaces());

        T instance =  (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                rpcInvoker);

        return instance;
    }

    @Override
    public ProxyCreate invoker(RpcInvoker invoker) {
        this.rpcInvoker = invoker;
        return this;
    }
}
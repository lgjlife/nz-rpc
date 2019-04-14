package com.nz.rpc.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;

/**
 *功能描述
 * @author lgj
 * @Description  cglib
 * @date 4/14/19
*/
@Slf4j
public class CglibProxyCreate implements ProxyCreate {

    private  RpcInvoker rpcInvoker;


    public <T> T createInstance(Class<T> interfaceClass){

        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(rpcInvoker);
        enhancer.setSuperclass(interfaceClass);
        T instance = (T) enhancer.create();
        return instance;
    }

    @Override
    public ProxyCreate invoker(RpcInvoker invoker) {
        this.rpcInvoker = invoker;
        return this;
    }

}
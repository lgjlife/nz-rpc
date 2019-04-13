package com.nz.rpc.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcProxyFactory {

    private RpcInvoker rpcInvoker = new RpcInvoker();

    public <T> T createInstance(Class<T> interfaceClass) {
        return createInstance(interfaceClass, false);
    }

    @SuppressWarnings("unchecked")
    public <T> T createInstance(Class<T> cls, boolean isTargetClass) {
        if (isTargetClass) {
            log.info("use cglib : " + cls.getSimpleName());
            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(rpcInvoker);
            enhancer.setSuperclass(cls);
            T instance = (T) enhancer.create();
            return instance;
        } else {
            log.info("use jdk dynamic proxy : " + cls.getSimpleName());
            log.debug("getClassLoader = " + cls.getClassLoader()
                    + " getInterfaces  =   " + cls.getInterfaces());
            log.debug("rpcInvoker is null ?  = " + (rpcInvoker == null));

            T instance =  (T) Proxy.newProxyInstance(cls.getClassLoader(),
                    // cls.getInterfaces(),
                    new Class[]{cls},
                    rpcInvoker);

            return instance;
        }
    }
}

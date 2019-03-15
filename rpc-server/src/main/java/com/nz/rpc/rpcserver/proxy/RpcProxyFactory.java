package com.nz.rpc.rpcserver.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
@Slf4j
public class RpcProxyFactory {
    @Autowired
    private RpcInvoker rpcInvoker;

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
            return (T) enhancer.create();
        } else {
            log.info("use jdk dynamic proxy : " + cls.getSimpleName());
            return (T) Proxy.newProxyInstance(cls.getClassLoader(),
                    new Class[]{cls}, rpcInvoker);
        }
    }

}

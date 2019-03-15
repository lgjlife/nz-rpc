package com.nz.rpc.client.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Slf4j
@Component
public class RpcProxyFactory {

    {
        log.debug("RpcProxyFactory 创建bean..............");
    }


   // @Autowired
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
            return (T) enhancer.create();
        } else {
            log.info("use jdk dynamic proxy : " + cls.getSimpleName());
            log.debug("getClassLoader = " + cls.getClassLoader()
                    + " getInterfaces  =   " + cls.getInterfaces());
            log.debug("rpcInvoker is null ?  = " + (rpcInvoker == null));
            return (T) Proxy.newProxyInstance(cls.getClassLoader(),
                    // cls.getInterfaces(),
                    new Class[]{cls},
                    rpcInvoker);
        }
    }
}

package com.nz.rpc.client.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 功能描述
 *
 * @author lgj
 * @Description 动态代理
 * @date 1/27/19
 */

@Slf4j
public class RpcInvoker implements InvocationHandler, MethodInterceptor {
    {

        log.debug("RpcInvoker 创建bean..............");
    }

    /**
     * 功能描述
     *
     * @author lgj
     * @Description jdk
     * @date 1/27/19
     * @param:
     * @return:
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doIncoke(method, args);
    }


    /**
     * 功能描述
     *
     * @author lgj
     * @Description cglib
     * @date 1/27/19
     * @param:
     * @return:
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return doIncoke(method, objects);
    }

    public Object doIncoke(Method method, Object[] args) {
        Object result = null;

        log.debug("++++++++++++++++++++++++++++++++RpcInvoker  doIncoke .....");
        for(Object arg:args){
            log.debug("参数 = " + arg);
        }
        return "执行动态代理成功";
    }
}

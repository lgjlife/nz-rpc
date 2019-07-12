package com.nz.rpc.proxy;

import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.invocation.client.RpcClientInvocation;
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
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return doIncoke(method, args);
    }

    public Object doIncoke(Method method, Object[] args) throws Throwable{


        ClientInvocation clientInvocation = new RpcClientInvocation(method,args);
        Object result = clientInvocation.executeNext();
        return result;
    }


}

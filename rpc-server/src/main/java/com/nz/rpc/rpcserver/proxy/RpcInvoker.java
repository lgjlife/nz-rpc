package com.nz.rpc.rpcserver.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

@Slf4j
@Component
public class RpcInvoker implements InvocationHandler, MethodInterceptor {
    /* @Resource
     private RpcClient rpcClient;
     @Resource
     private RpcRequestPool requestPool;*/
    @Override
    public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {

        log.debug("RpcInvoker invoke.......... ");
        return doInvoke(method, parameters);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] parameters, MethodProxy proxy) throws Throwable {
        log.debug("RpcInvoker intercept.... ");
        return doInvoke(method, parameters);
    }

    private Object doInvoke(Method method, Object[] parameters) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();

       /* RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(requestId)
                .className(className)
                .methodName(methodName)
                .parameterTypes(parameterTypes)
                .parameters(parameters).build();
        rpcClient.send(rpcRequest);
        RpcResponse response = requestPool.getResponse(requestId);
        Object result = response.getResult();
        if (result == null){
            throw response.getException();
        }
        //json会将对象内部的Object对象反序列化为Map形式，这里需要手动cast result类型
        if (result instanceof Map){
            result = TypeUtils.cast(result, method.getReturnType(), null);
        }
        return result;*/
        return null;
    }
}

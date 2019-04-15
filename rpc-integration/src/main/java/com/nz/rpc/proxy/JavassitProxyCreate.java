package com.nz.rpc.proxy;


import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 *功能描述
 * @author lgj
 * @Description  javassit　　存在问题，无法代理接口!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * @date 4/14/19
*/
@Slf4j
public class JavassitProxyCreate implements ProxyCreate {

    private  RpcInvoker rpcInvoker;

    public <T> T createInstance(Class<T> interfaceClass){

        return  getProxy(interfaceClass);
    }

    public <T> T getProxy(Class<T> interfaceClass){


        ProxyFactory proxyFactory  = new ProxyFactory();
        try{

            proxyFactory.setSuperclass(interfaceClass);
            proxyFactory.setHandler(new MethodHandler() {
                public Object invoke(Object proxy, Method method, Method method1, Object[] args) throws Throwable {
                    //method 被代理类的方法
                    //method1代理类的方法　使用这个方法
                    Object result =  (T)rpcInvoker.invoke(proxy,method1,args);
                    return  result;

                }
            });
            T bean =  (T)proxyFactory.createClass().newInstance();
            return  bean;
        }
        catch(Exception ex){
            log.error("Javassit 创建代理失败:{}",ex.getMessage());
            return null;
        }



    }

    @Override
    public ProxyCreate invoker(RpcInvoker invoker) {
        this.rpcInvoker = invoker;
        return this;
    }

}
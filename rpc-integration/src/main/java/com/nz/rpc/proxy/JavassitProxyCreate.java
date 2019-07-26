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

        if(interfaceClass.isInterface()){
            Class[] clz  = new Class[1];
            clz[0] = interfaceClass;
            proxyFactory.setInterfaces(clz);
        }
        else {
            proxyFactory.setSuperclass(interfaceClass);
        }


        proxyFactory.setHandler(new MethodHandler() {
            public Object invoke(Object proxy, Method method, Method method1, Object[] args) throws Throwable {
                //method 被代理类的方法
                //method1代理类的方法　使用这个方法

                Object result =  (T)rpcInvoker.invoke(proxy,method1,args);
                return  result;

            }
        });

        try{

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

    public static void main(String args[]){

        JavassitProxyCreate create = new JavassitProxyCreate();
        Demo3 de = create.createInstance(Demo3.class);
        de.func();

    }


    interface Demo{
        void func();
    }

    class Demo1{
        void func(){}

    }

}
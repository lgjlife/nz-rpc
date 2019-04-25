package com.nz.rpc.invocation.client;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.interceptor.InterceptorChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class RpcClientInvocationTest {

    InterceptorChain interceptorChain;


    @Before
    public  void before(){
        interceptorChain = new InterceptorChain();
    }


    @Test
    public void executeNext() {

        Class clz =   Demo.class;


        RpcClientInvocation invocation = null;

        try{

            Method method = clz.getMethod("func",String.class);
            Object[] args = new String[1];
            args[0] = "libai";

            interceptorChain.addFirst("time",new TimeInterceptor());
            interceptorChain.addBefore("time","log",new LogInterceptor());
            interceptorChain.addLast("request",new RpcRequestInterceptor());

            List<Interceptor> interceptors = interceptorChain.getInterceptor();

            System.out.println("interceptors = " + interceptors);
            invocation  = new RpcClientInvocation(method,args,interceptors);

            Object result = invocation.executeNext();

            log.info("result = " + result);

        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

    }
}

class Demo{

    public String func(String name){

        return  name;
    }
}
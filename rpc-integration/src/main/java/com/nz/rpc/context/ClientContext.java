package com.nz.rpc.context;

import com.nz.rpc.interceptor.Interceptor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClientContext {

    /**
     * 请求责任链拦截器列表
     */
    public static List<Interceptor> interceptors;
    /**
     * 异步方法列表,全限定接口名+方法名
    */
    private static Set<Method> asyncMethods = new HashSet<>();

    /**
     * 异步调用CompletableFuture
    */
    private static Map<Long,CompletableFuture> futureMap = new ConcurrentHashMap<>();

    //private static Map<String,String>



    /**
     *功能描述
     * @author lgj
     * @Description  添加异步方法
     * @date 5/6/19
     * @param:
     * @return:
     *
    */
    public static void addAsyncMethod(Method  method){
        asyncMethods.add(method);
    }
    /**
     *功能描述
     * @author lgj
     * @Description  判断方法是否是异步请求　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
     * @date 5/6/19
     * @param:
     * @return:
     *
    */
    public static boolean isAsync(Method  method){
        return asyncMethods.contains(method);
    }


    /**
     *功能描述
     * @author lgj
     * @Description  设置future
     * @date 5/6/19
     * @param:  id:每次rpc请求的唯一id
     * @return:
     *
    */
    public static CompletableFuture createCompletableFuture(long id){
        CompletableFuture future = new CompletableFuture();
        futureMap.put(id,future);
        return future;
    }

    public static CompletableFuture getCompletableFuture(long id){

        return futureMap.get(id);
    }

    public static void removeCompletableFuture(long id){

        futureMap.remove(id);
    }

}

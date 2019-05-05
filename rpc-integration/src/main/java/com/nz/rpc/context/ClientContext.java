package com.nz.rpc.context;

import com.nz.rpc.interceptor.Interceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientContext {

    public static List<Interceptor> interceptors;


    public static boolean isAsync(Method  method){

        return true;
    }


    public static void setCompletableFuture(long id,CompletableFuture future){

    }

    public static CompletableFuture getCompletableFuture(long id){

        return null;
    }

    public static void removeCompletableFuture(long id){


    }

}

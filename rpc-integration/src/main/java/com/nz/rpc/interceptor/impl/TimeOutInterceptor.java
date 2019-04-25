package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TimeOutInterceptor implements Interceptor {


    private ThreadLocal<ReentrantLock> lockThreadLocal = new ThreadLocal<>();


    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        log.info("正在执行TimeInterceptor....");
        long start = System.currentTimeMillis();
        lockThreadLocal.set(new ReentrantLock());
        Object result = invocation.executeNext();

        long end = System.currentTimeMillis();
        log.info("方法[{}]调用时间＝[{}]",invocation.getMethod().getName(),end-start);
        log.info("TimeInterceptor执行结束");
        return result;
    }
}

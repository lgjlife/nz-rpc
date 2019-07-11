package com.nz.rpc.invocation.client;

import com.nz.rpc.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeInterceptor implements Interceptor {

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        log.info("正在执行TimeInterceptor....");
        long start = System.currentTimeMillis();
        Object result = invocation.executeNext();
        long end = System.currentTimeMillis();
        log.info("方法[{}]调用时间＝[{}]",invocation.getMethod().getName(),end-start);
        log.info("TimeInterceptor执行结束");
        return result;
    }
}

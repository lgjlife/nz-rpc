package com.nz.rpc.invocation.client;

import com.nz.rpc.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements Interceptor {

    @Override
    public Object intercept(ClientInvocation invocation) throws RuntimeException {
        log.info("正在执行LogInterceptor[{}]....",invocation.getMethod().getName());
        Object result = invocation.executeNext();
        log.info("LogInterceptor执行结束");
        return result;
    }
}

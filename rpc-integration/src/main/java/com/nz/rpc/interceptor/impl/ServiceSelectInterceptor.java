package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.msg.ClientMessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceSelectInterceptor implements Interceptor {


    private  ClientMessageHandler handler;
    public ServiceSelectInterceptor(ClientMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        log.info("正在执行ServiceSelectInterceptor....");
        handler.serviceSelect(invocation);
        Object result = invocation.executeNext();
        return result;
    }
}

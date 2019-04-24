package com.nz.rpc.invocation.client;

import com.nz.rpc.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RpcRequestInterceptor implements Interceptor {

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        String result = "1234556";
        log.info("请求参数:",invocation.getArgs());
        return result;
    }
}

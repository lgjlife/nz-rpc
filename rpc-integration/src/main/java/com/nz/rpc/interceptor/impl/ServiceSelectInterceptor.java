package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.loadbalance.LoadbanlanceHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述
 * @author lgj
 * @Description  负载均衡责任链处理
 * @date 5/6/19
*/
@Slf4j
public class ServiceSelectInterceptor implements Interceptor {



    private LoadbanlanceHandler loadbanlanceHandler;



    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        log.debug("ServiceSelectInterceptor start ....");
        //负载均衡选择Server
        loadbanlanceHandler.serviceSelect(invocation);
        Object result = invocation.executeNext();
        return result;
    }



    public void setLoadbanlanceHandler(LoadbanlanceHandler loadbanlanceHandler) {
        this.loadbanlanceHandler = loadbanlanceHandler;
    }
}

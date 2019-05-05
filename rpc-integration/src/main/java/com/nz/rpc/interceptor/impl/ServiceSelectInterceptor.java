package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.msg.ClientMessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述
 * @author lgj
 * @Description  负载均衡责任链处理
 * @date 5/6/19
*/
@Slf4j
public class ServiceSelectInterceptor implements Interceptor {


    private  ClientMessageHandler handler;
    public ServiceSelectInterceptor(ClientMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

       // log.info("正在执行ServiceSelectInterceptor....");
        try{
            handler.serviceSelect(invocation);
        }
        catch(Exception ex){
            log.error(ex.getMessage());

            return null;
        }

        Object result = invocation.executeNext();
        return result;
    }
}

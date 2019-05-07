package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述
 * @author lgj
 * @Description  集群容错处理
 * @date 5/7/19
*/

@Slf4j
public class ClusterFaultToleranceInterceptor implements Interceptor {

    @Override
    public Object intercept(ClientInvocation invocation) throws RuntimeException {
        Object result = null;
        log.debug("ClusterFaultToleranceInterceptor start ....");
        try{
            result = invocation.executeNext();
        }
        catch(Exception ex){
            log.error("Cluster is Fault!!!" + ex.getMessage());
        }

        return result;

    }
}

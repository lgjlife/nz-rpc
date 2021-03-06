package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理
 * @date 5/8/19
*/
@Slf4j
public class FailoverClusterFault implements ClusterFault {

    public  Object doHandle(ClientInvocation invocation,Exception ex){

        if(log.isErrorEnabled()){
            log.error("Failover:Request[{}] error,ex = [{}]",invocation.getMethod(),ex.getMessage());
        }

        return null;
    }
}

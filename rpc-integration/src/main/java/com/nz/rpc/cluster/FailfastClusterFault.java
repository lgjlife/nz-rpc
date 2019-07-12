package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理,快速失败，失败后 立即抛出异常
 * @date 5/8/19
*/
@Slf4j
public class FailfastClusterFault implements ClusterFault {

    public  Object doHandle(ClientInvocation invocation,Exception ex) throws Exception{
        log.error("Failfast:Request[{}] error,ex = [{}]",invocation.getMethod(),ex.getMessage());
        throw ex;
    }
}

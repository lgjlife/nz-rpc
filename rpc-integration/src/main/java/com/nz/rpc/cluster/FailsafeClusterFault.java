package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理 所谓的失败安全是指，当调用过程中出现异常时，
 * 仅会打印异常，而不会抛出异常。适用于写入审计日志等操作
 * @date 5/8/19
*/

@Slf4j
public class FailsafeClusterFault implements ClusterFault {



    public  Object doHandle(ClientInvocation invocation,Exception ex){
        if(log.isErrorEnabled()){
            log.error("Failsave:Request[{}] error,ex = [{}]",invocation.getMethod(),ex.getMessage());
        }

        return null;
    }
}

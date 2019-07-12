package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理
 * @date 5/8/19
*/
public class FailsaveClusterFault implements ClusterFault {

    public  Object doHandle(ClientInvocation invocation,Exception ex){

        return null;
    }
}

package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理　Failback Cluster
 * 失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
 * @date 5/8/19
*/
public class FailbackClusterFaultToleranceHandle implements ClusterFaultToleranceHandle {

    public  void doHandle(ClientInvocation invocation){

    }
}

package com.nz.rpc.interceptor.impl;

import com.nz.rpc.cluster.ClusterFaultHandler;
import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述
 * @author lgj
 * @Description  集群容错处理
 *
 * 出错原因：
 * １.服务端关闭channel，或者服务端宕机
 * 2.服务端拒绝请求(请求太多，无法处理),发生IO-EXCEPTION
 * 3.服务端执行任务超时
 * 4.服务端执行任务出现异常
 *
 *　容错处理：
 * 一.　Ｆailover 失败自动切换
 * 　　　使用场景：
 *          １.读操作，读操作是幂等性操作
 *          2. 幂等性服务，调用一次和调用多次结果一样
 *       缺点： 1.延迟较大
 *
 * 二.Failfast Cluster
 * 快速失败，只发起一次调用，失败立即报错。通常用于非幂等性的写操作，比如新增记录。
 * 在业务高峰期，对于一些非核心的服务，希望只调用一次，失败也不再重试，为重要的核心服务节约宝贵的运行资源
 *
 * 三.Failsafe Cluster
 * 失败安全，出现异常时，直接忽略。通常用于写入审计日志等操作。
 *
 * 四.Failback Cluster
 * 失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
 *
 *　 在集群容错设计的时候，需要考虑扩展性，主要从以下几方面进行设计：
 *　　1）、容错接口的开放。
 *   2）、屏蔽底层细节，用户定制简单。
 *   3）、配置应当支持扩展，不要让用户扩展服务框架Schema。
 * @date 5/7/19
*/

@Slf4j
public class ClusterFaultToleranceInterceptor implements Interceptor {

    private ClusterFaultHandler clusterFaultHandler;

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {
        Object result = null;
        log.debug("ClusterFaultToleranceInterceptor start ....");
        try{
            result = invocation.executeNext();
        }
        catch(Exception ex){
            if(log.isErrorEnabled()){
                log.error("Cluster is Fault!!!" + ex.getMessage());
            }


            clusterFaultHandler.handle(invocation,ex);

        }

        return result;

    }

    public void setClusterFaultHandler(ClusterFaultHandler clusterFaultHandler) {
        this.clusterFaultHandler = clusterFaultHandler;
    }
}

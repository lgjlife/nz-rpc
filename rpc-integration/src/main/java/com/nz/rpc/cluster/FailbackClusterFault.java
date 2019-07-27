package com.nz.rpc.cluster;

import com.nz.rpc.invocation.client.ClientInvocation;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错处理　Failback Cluster
 * 失败自动恢复，后台记录失败请求，定时重发。通常用于消息通知操作。
 * @date 5/8/19
*/
@Slf4j
public class FailbackClusterFault implements ClusterFault {

    //重试间隔
    private static final long RETRY_FAILED_PERIOD = 5 * 1000;
    private final ScheduledExecutorService retryExecutorService= Executors.newSingleThreadScheduledExecutor();
    private Map<String,ScheduledFuture> futureMap = new ConcurrentHashMap<>();


    public  Object doHandle(ClientInvocation invocation,Exception ex) throws Exception{

        final String uid  =  UUID.randomUUID().toString();

        if(log.isErrorEnabled()){
            log.error("Failback:Request[{}] error,ex = [{}]",invocation.getMethod(),ex.getMessage());
        }


        ScheduledFuture<?>  future =  retryExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {


                try{
                    invocation.resetIndex();
                    invocation.executeNext();
                    ScheduledFuture<?>  future = futureMap.get(uid);
                    future.cancel(false);
                    futureMap.remove(uid);
                }
                catch(Exception ex){
                    log.error(ex.getMessage());

                }

            }
        },RETRY_FAILED_PERIOD,RETRY_FAILED_PERIOD, TimeUnit.MILLISECONDS);
        futureMap.put(uid,future);

        return null;
    }


}

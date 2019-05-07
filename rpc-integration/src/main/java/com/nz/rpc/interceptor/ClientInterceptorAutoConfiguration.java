package com.nz.rpc.interceptor;


import com.nz.rpc.context.ClientContext;
import com.nz.rpc.interceptor.impl.ClusterFaultToleranceInterceptor;
import com.nz.rpc.interceptor.impl.RpcClientRequestInterceptor;
import com.nz.rpc.interceptor.impl.ServiceSelectInterceptor;
import com.nz.rpc.interceptor.impl.TimeOutInterceptor;
import com.nz.rpc.msg.ClientMessageHandler;
import com.nz.rpc.uid.UidProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ClientInterceptorAutoConfiguration {

    @Autowired
    private UidProducer uidProducer ;

    @Autowired
    private ClientMessageHandler handler;

    @Bean
    public InterceptorChain interceptorChain(){
        InterceptorChain interceptorChain = new InterceptorChain();

        try{
            //集群容错处理
            interceptorChain.addFirst("ClusterFaultToleranceInterceptor",new ClusterFaultToleranceInterceptor());
            //超时处理
            interceptorChain.addAfter("ClusterFaultToleranceInterceptor","TimeOutInterceptor",new TimeOutInterceptor());
            //负载均衡处理
            interceptorChain.addLast("ServiceSelectInterceptor",new ServiceSelectInterceptor(handler));
            //数据RPC发送处理
            interceptorChain.addAfter("ServiceSelectInterceptor","RpcClientRequestInterceptor",new RpcClientRequestInterceptor(uidProducer,handler));
            ClientContext.interceptors = interceptorChain.getInterceptor();
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }


        return interceptorChain;
    }
}

package com.nz.rpc.msg;

import com.nz.rpc.constans.RpcClientConstans;
import com.nz.rpc.context.ClientContext;
import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.discover.ProviderConfigContainer;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.loadbalance.LoadbalanceStrategy;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Slf4j
public class ClientMessageHandler {


    private static ClientMessageHandler instance= new ClientMessageHandler();

    private NettyClient nettyClient;
    private LoadbalanceStrategy  loadbalanceStrategy;

    private Map<Long,RpcResponse> resultMap = new ConcurrentHashMap<>();
    private static  final int requestTimeoutMs = 30000;
    private Map<Long,RequestLock> requestLock = new ConcurrentHashMap<>();

    public static ClientMessageHandler getInstance() {
        return instance;
    }
    /**
     *功能描述
     * @author lgj
     * @Description 　负载均衡-选择服务提供者
     * @date 4/25/19
     * @param:
     * @return:
     *
    */
    public void serviceSelect(ClientInvocation invocation) throws Exception{

        //获取消费者
        Map<String, ProviderConfig> configMap = ProviderConfigContainer.getConfigMap();
        List<ProviderConfig> registryConfigLists = new ArrayList<>();
        configMap.forEach((k,v)->{
            if(v.getInterfaceName().equals(invocation.getMethod().getDeclaringClass().getName())){
                registryConfigLists.add(v);
            }
        });
        //
        log.debug("服务提供者信息:{}",registryConfigLists);

        //负载均衡处理
        ProviderConfig registryConfig =  loadbalanceStrategy.select(registryConfigLists,null);
        log.debug("负载均衡选择结果 = " + registryConfig);

        invocation.getAttachments().put(RpcClientConstans.NETTY_REQUEST_HOST,registryConfig.getHost());
        invocation.getAttachments().put(RpcClientConstans.NETTY_REQUEST_PORT,registryConfig.getPort().toString());


    }

    /**
     *功能描述
     * @author lgj
     * @Description  发送请求　
     * @date 4/25/19
     * @param:
     * @return:
     *
    */
    public Object sendRequest(String host,String port,NettyMessage nettyMessage){
        log.debug("sendRequest [{}]-[{}]-[{}]",host,port,nettyMessage);

        long id = ((RpcRequest)nettyMessage.getBody()).getRequestId();

        Channel channel = nettyClient.getChannel(host,
                Integer.valueOf(port));
        if(channel == null){
            log.debug("sendRequest:channel is null,reconnect ....");
            new  Thread(){
                @Override
                public void run() {
                    nettyClient.connect(host,Integer.valueOf(port));
                }
            }.start();
            return null;
        }
        else {
            //发送消息
            ChannelFuture future = channel.writeAndFlush(nettyMessage);
            requestLock.put(id,new RequestLock());

            return null;
            // return getServerResponseResult(host,port,nettyMessage);
        }
    }

    /**
     *功能描述
     * @author lgj
     * @Description  获取返回结果
     * @date 4/25/19
     * @param:
     * @return:
     *
    */
    public Object getServerResponseResult( long id){

        //long id = ((RpcRequest)nettyMessage.getBody()).getRequestId();

        ReentrantLock lock = requestLock.get(id).getLock();

        Condition condition =  requestLock.get(id).getCondition();
        Object result = null;
        try{
            lock.lock();
            boolean flag = condition.await(requestTimeoutMs, TimeUnit.MILLISECONDS);
            if(flag){
                //请求响应成功
                log.debug("server response []",resultMap.get(id).getResult());
                result = resultMap.get(id).getResult();
            }
            else {
                //请求响应超时
                log.error("server response time out,removeChannel and reconnect...");
                //关闭channel
                //nettyClient.removeChannel(host,Integer.valueOf(port));
                //发起重新连接
                //nettyClient.connect(host,Integer.valueOf(port));

                result = null;
            }
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            lock.unlock();
        }
        //requestLock.remove(id);
        return result;
    }

    /**
     *功能描述 
     * @author lgj　
     * @Description
     * @date 4/25/19　
     * @See　　
     * */
    public void recvResponse(RpcResponse response){
        log.info("response = "+response);
        long id = response.getResponseId();
        CompletableFuture future = ClientContext.getCompletableFuture(id);
        if(future != null){
            //不是异步请求
            future.complete(response.getResult());
            ClientContext.removeCompletableFuture(id);
        }
        else {
            //同步请求处理
            ReentrantLock lock = requestLock.get(id).getLock();
            Condition condition = requestLock.get(id).getCondition();
            try{
                lock.lock();
                resultMap.put(id,response);
                condition.signalAll();
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }
            finally{
                lock.unlock();
            }
        }


    }


    @Data
    class RequestLock{

        private ReentrantLock lock;
        private Condition condition;


        public RequestLock() {
            this.lock = new ReentrantLock();
            this.condition = lock.newCondition();

        }
    }

}

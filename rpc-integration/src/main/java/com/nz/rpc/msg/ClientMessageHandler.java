package com.nz.rpc.msg;

import com.nz.rpc.constans.RpcClientConstans;
import com.nz.rpc.context.ClientContext;
import com.nz.rpc.exception.MessageSendFailException;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *功能描述 
 * @author lgj
 * @Description 客户端消息处理类
 * @date 7/12/19
*/
@Data
@Slf4j
public class ClientMessageHandler {


    private static ClientMessageHandler instance= new ClientMessageHandler();

    private NettyClient nettyClient;


    private Map<Long,RpcResponse> resultMap = new ConcurrentHashMap<>();
    private static  final int requestTimeoutMs = 5000;
    private Map<Long,RequestLock> requestLock = new ConcurrentHashMap<>();

    //消息缓存列表
 //   private Queue<Message> messagesCache = new ConcurrentLinkedQueue<>();


    public static ClientMessageHandler getInstance() {
        return instance;
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
    public void sendRequest(String host,String port,NettyMessage nettyMessage) throws Exception{
        log.debug("sendRequest [{}]-[{}]-[{}]",host,port,nettyMessage);

        long id = ((RpcRequest)nettyMessage.getBody()).getRequestId();

        Channel channel = nettyClient.getChannel(host,
                Integer.valueOf(port));
        if(channel == null){
            log.error("Server [{}]-[{}] unconnect!",host,port);
            nettyClient.connect(host,Integer.valueOf(port));
            throw new MessageSendFailException("Server [" + host + "]-[" + port + "] unconnect!");
        }
        else {
            //发送消息
            ChannelFuture future = channel.writeAndFlush(nettyMessage);
            requestLock.put(id,new RequestLock());

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
    public Object getServerResponseResult( ClientInvocation invocation,long requestId){

        //long id = ((RpcRequest)nettyMessage.getBody()).getRequestId();

        ReentrantLock lock = requestLock.get(requestId).getLock();

        Condition condition =  requestLock.get(requestId).getCondition();
        Object result = null;
        try{
            lock.lock();
            boolean flag = condition.await(requestTimeoutMs, TimeUnit.MILLISECONDS);
            if(flag){
                //请求响应成功
                RpcResponse response = resultMap.get(requestId);
                resultMap.remove(requestId);
                if(log.isDebugEnabled()){
                    log.debug("server response [{}]",response);
                }


                Exception ex;
                if(( ex = response.getException()) != null){
                    if(log.isErrorEnabled()){
                        log.error("服务端执行请求出现错误!"+ex.getMessage());
                    }

                    throw ex;
                }
                result = response.getResult();

            }
            else {
                //请求响应超时
                if(log.isErrorEnabled()){
                    log.error("server response time out,removeChannel and reconnect...");

                }

                String host = invocation.getAttachments().get(RpcClientConstans.NETTY_REQUEST_HOST);
                Integer port = Integer.valueOf(invocation.getAttachments().get(RpcClientConstans.NETTY_REQUEST_PORT));

                //关闭channel
                //nettyClient.removeChannel(host,Integer.valueOf(port));
                //发起重新连接
                nettyClient.connect(host,port);

                result = null;
            }
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            lock.unlock();
            requestLock.remove(requestId);

        }
        //requestLock.remove(id);
        return result;
    }

    /**
     *功能描述 
     * @author lgj　
     * @Description 响应回调函数
     * @date 4/25/19　
     * @See　　
     * */
    public void responseCallback(RpcResponse response){


        long requestId = response.getResponseId();
        CompletableFuture future = ClientContext.getCompletableFuture(requestId);
        if(future != null){
            //是异步请求
           /// CompletableFuture responseFuture = (CompletableFuture)response.getResult();
            if(log.isInfoEnabled()){
                log.info("异步请求 response = "+response);
            }

            try{
                future.complete(response.getResult());
                ClientContext.removeCompletableFuture(requestId);
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }

        }
        else {
            //同步请求处理

            if(log.isInfoEnabled()){
                log.info("同步请求 response = "+response);
            }

            ReentrantLock lock = requestLock.get(requestId).getLock();
            Condition condition = requestLock.get(requestId).getCondition();
            try{
                lock.lock();
                resultMap.put(requestId,response);
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

    @Data

    private class Message{

        private String host;
        private int port;
        private NettyMessage nettyMessage;

        public Message(String host, int port, NettyMessage nettyMessage) {
            this.host = host;
            this.port = port;
            this.nettyMessage = nettyMessage;
        }
    }
}

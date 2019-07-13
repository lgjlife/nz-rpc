package com.nz.rpc.msg;

import com.nz.rpc.executor.TraceExecutorService;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 *功能描述
 * @author lgj
 * @Description  RPC请求消息处理
 * @date 5/7/19
*/
@Slf4j
public class ServerMessageHandler {

    private static  ServerMessageHandler serverMessageHandler = new ServerMessageHandler();

    //处理线程池定义
    private  static  ThreadPoolExecutor executorService = new TraceExecutorService(100,100,
                                                                        1000, TimeUnit.MICROSECONDS,
                                                                                     new LinkedBlockingQueue<Runnable>(),
                                                                                      new ThreadPoolExecutor.CallerRunsPolicy());


    private ServerMessageHandler(){

    }

    public static  ServerMessageHandler getInstance(){
        return  serverMessageHandler;
    }
    
    /**
     *功能描述 
     * @author lgj
     * @Description 提交任务到线程池
     * @date 7/12/19
     * @param:  
     * 
     * @return: 
     * 
     *
    */
    public   void submit(ChannelHandlerContext ctx,RpcRequest request){
        Future<?> future = executorService.submit(new RequestHandler(ctx,request));
        log.debug("executorService = {}",executorService.toString());
    }


    /**
     *功能描述 
     * @author lgj
     * @Description  线程池任务类
     * @date 7/12/19
    */
    class RequestHandler implements  Runnable{

        private  ChannelHandlerContext ctx;
        private  RpcRequest request;

        public RequestHandler(ChannelHandlerContext ctx, RpcRequest request) {
            this.ctx = ctx;
            this.request = request;
        }

        @Override
        public void run() {
            NettyMessage nettyMessage  = null;

            try{
                Object result = doInvoke(request);
                nettyMessage =   buildNettyMessage(result,null);
            }
            catch(Exception ex){
                log.error(ex.getMessage());
                nettyMessage = buildNettyMessage(null,ex);
            }
            ctx.writeAndFlush(nettyMessage);

        }

        /**
         *功能描述
         * @author lgj
         * @Description  构建消息
         * @date 5/7/19
         * @param: 　result: 方法调用结果　　ex:方法调用异常
         * @return: 　NettyMessage
         *
        */
        private NettyMessage buildNettyMessage(Object result,Exception ex){
            NettyMessage  nettyMessage = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.APP_RESPONE_TYPE.getValue());
            nettyMessage.setHeader(header);
            RpcResponse response = new RpcResponse();
            response.setResponseId(request.getRequestId());
            response.setResult(result);
            response.setException(ex);
            nettyMessage.setBody(response);
            return nettyMessage;
        }

        /**
         *功能描述
         * @author lgj
         * @Description  执行反射调用
         * @date 5/7/19
         * @param:
         * @return:
         *
        */
        private  Object doInvoke(RpcRequest request) throws Exception{

            log.debug("request = " + request);
            String clzImplName = NettyContext.getLocalServiceImpl(request.getInterfaceName());
            Class clzImpl = Class.forName(clzImplName);

            Object bean = clzImpl.newInstance();

            Class[] paramTypes = new Class[request.getParameterTypes().length];
            for(int i = 0; i< request.getParameterTypes().length ; i++){
                paramTypes[i] = Class.forName(request.getParameterTypes()[i]);
            }

            Method method = clzImpl.getDeclaredMethod(request.getMethodName(),paramTypes);

            Object result =  method.invoke(bean,request.getParameters());

            //异步调用处理
            //判断返回值是否是CompletableFuture类型
            if(CompletableFuture.class.isAssignableFrom(result.getClass())){
                //异步调用
                result =  ((CompletableFuture) result).get();
            }
            log.debug("method [{}] done !result = [{}]",method,result);
            return result;
        }
    }



}

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

    private  static  ThreadPoolExecutor executorService = new TraceExecutorService(100,100,
            1000, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<Runnable>(),
     new ThreadPoolExecutor.CallerRunsPolicy());


    private ServerMessageHandler(){

    }

    public static  ServerMessageHandler getInstance(){
        return  serverMessageHandler;
    }


    public   void submit(ChannelHandlerContext ctx,RpcRequest request){
        Future<?> future = executorService.submit(new RequestHandler(ctx,request));
        log.debug("executorService = {}",executorService.toString());
    }


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
                buildNettyMessage(result,null);
            }
            catch(Exception ex){
                log.error(ex.getMessage());
                buildNettyMessage(null,ex);
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
            header.setType(MessageType.APP_RESPONE_TYPE);
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
            String clzImplName = NettyContext.getLocalServiceImplMap().get(request.getInterfaceName());
            Class clzImpl = Class.forName(clzImplName);

            Object bean = clzImpl.newInstance();

            Class[] paramTypes = new Class[request.getParameterTypes().length];
            for(int i = 0; i< request.getParameterTypes().length ; i++){
                paramTypes[i] = Class.forName(request.getParameterTypes()[i]);
            }

            Method method = clzImpl.getDeclaredMethod(request.getMethodName(),paramTypes);

            Object result =  method.invoke(bean,request.getParameters());
            if(CompletableFuture.class.isAssignableFrom(result.getClass())){
                //异步调用
                result =  ((CompletableFuture) result).get();
            }
            log.debug("method [{}] done !result = [{}]",method,result);
            return result;
        }
    }



}

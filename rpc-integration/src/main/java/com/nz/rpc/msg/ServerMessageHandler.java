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

            Object result = doInvoke(request);

            NettyMessage  nettyMessage = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.APP_RESPONE_TYPE);
            nettyMessage.setHeader(header);
            RpcResponse response = new RpcResponse();
            response.setResponseId(request.getRequestId());
            response.setResult(result);
            nettyMessage.setBody(response);

            ctx.writeAndFlush(nettyMessage);

        }


        private  Object doInvoke(RpcRequest request){

            try{
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
               log.debug("method [{}] done ![{}]",method,result);
               return result;
            }
            catch(Exception ex){
                log.error("{}",ex.getMessage());
            }
            finally{

            }


            return  null;
        }
    }



}

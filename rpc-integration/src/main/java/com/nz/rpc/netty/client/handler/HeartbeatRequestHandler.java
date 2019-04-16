package com.nz.rpc.netty.client.handler;

import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 *功能描述 
 * @author lgj
 * @Description  心跳请求处理类
 * @date 4/16/19
*/
@Slf4j
public class HeartbeatRequestHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Integer> timeOutCount = new HashMap<>();
    private Map<String,ScheduledFuture> futureTaskMap = new HashMap<>();
    private static final  int heartbeatRateTimeSecond = 5;
    private static final   int heartbeatTimeOutSecond = 10;

    private NettyClient nettyClient;


    private HeartbeatRequestHandler() {

    }

    public HeartbeatRequestHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ScheduledFuture futureTask  = ctx.executor().scheduleAtFixedRate(new HeartbeatRequestHandler.HeartbeatTask(ctx),0,5, TimeUnit.SECONDS);
        futureTaskMap.put(getAddress(ctx),futureTask);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage)msg;
        if((message.getHeader() != null)
           &&(message.getHeader().getType() == MessageType.HEARTBEAT_RESPONSE_TYPE)){
            log.debug("接收到[{}]的心跳响应消息",getAddress(ctx));
            timeOutCount.put(getAddress(ctx),0);

        }
        else {
            super.channelRead(ctx, msg);
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("HeartbeatRequestHandler exceptionCaught");
        super.exceptionCaught(ctx, cause);
    }

    private  class   HeartbeatTask implements  Runnable{

        private  ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {

            Integer count = timeOutCount.get(getAddress(ctx));

            if((count != null)&&((++count)*heartbeatRateTimeSecond > heartbeatTimeOutSecond)){
                log.debug("与[{}]已经断开连接",getAddress(ctx));
                ScheduledFuture future = futureTaskMap.get(getAddress(ctx));
                nettyClient.connect(ctx.channel().remoteAddress());
                future.cancel(true);
                futureTaskMap.remove(getAddress(ctx));

            }
            else {
                count = (count==null)?1:count;

                timeOutCount.put(getAddress(ctx),count);
                log.debug("给[{}]发送心跳消息",getAddress(ctx));
                NettyMessage nettyMessage = buildHeartbeatMessage();
                ctx.writeAndFlush(nettyMessage);
            }

        }
    }


    /**
     *功能描述
     * @author lgj
     * @Description  构建心跳消息
     * @date 4/16/19
     * @param:
     * @return:
     *
    */
    private NettyMessage buildHeartbeatMessage(){
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_REQUEST_TYPE);
        nettyMessage.setHeader(header);
        return  nettyMessage;
    }

    private String getAddress(ChannelHandlerContext ctx){
        return ctx.channel().remoteAddress().toString();
    }
}

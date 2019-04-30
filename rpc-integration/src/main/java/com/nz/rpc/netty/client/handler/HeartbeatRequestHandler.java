package com.nz.rpc.netty.client.handler;

import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;


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
    private static final  int heartbeatRateTimeSecond = 20;
    private static final   int heartbeatTimeOutSecond = 50;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

       // ScheduledFuture futureTask  = ctx.executor().scheduleAtFixedRate(new HeartbeatRequestHandler.HeartbeatTask(ctx),0,heartbeatRateTimeSecond, TimeUnit.SECONDS);
      //  futureTaskMap.put(getAddress(ctx),futureTask);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage)msg;
        if((message.getHeader() != null)
           &&(message.getHeader().getType() == MessageType.HEARTBEAT_RESPONSE_TYPE)){
          //  log.debug("接收到[{}]的心跳响应消息",getAddress(ctx));
          //  timeOutCount.put(getAddress(ctx),0);

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
                NettyContext.getNettyClient().connect(ctx.channel().remoteAddress());
                future.cancel(true);
                futureTaskMap.remove(getAddress(ctx));
                ctx.channel().close();

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
     * @Description  读写空闲事件
     * @date 4/26/19
     * @param:
     * @return:
     *
    */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            //log.debug("userEventTriggered....[{}]",event.state());


            if (event.state()== IdleState.READER_IDLE){
                log.warn("channel[{}]连接超时,关闭channel!" ,ctx.channel());
                NettyContext.getNettyClient().connect(ctx.channel().remoteAddress());
                ChannelFuture channelFuture = ctx.channel().close();
                channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        Channel channel  = channelFuture.channel();

                        log.debug("channel[{}]状态[{}]",channel.remoteAddress(),channel.isActive());
                    }
                });

            }
            else if (event.state()== IdleState.WRITER_IDLE){
                NettyMessage nettyMessage = buildHeartbeatMessage();
                ctx.writeAndFlush(nettyMessage);
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
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

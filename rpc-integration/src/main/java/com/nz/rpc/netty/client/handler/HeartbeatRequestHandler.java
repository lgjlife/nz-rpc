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

import java.util.concurrent.ConcurrentHashMap;


/**
 *功能描述 
 * @author lgj
 * @Description  心跳请求处理类
 * @date 4/16/19
*/
@Slf4j
public class HeartbeatRequestHandler extends ChannelInboundHandlerAdapter {

    private final int MAX_HEARTBEAT_FAIL_COUNT = 3;

    private ConcurrentHashMap<String,Integer> maxHeartbeatFailCountRecords = new ConcurrentHashMap<String, Integer>();



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage)msg;
        if((message.getHeader() != null)
           &&(message.getHeader().getType() == MessageType.HEARTBEAT_RESPONSE_TYPE.getValue())){

          if (log.isDebugEnabled()){
              log.debug("接收到[{}]的心跳响应消息",getAddress(ctx));
          }
          //  timeOutCount.put(getAddress(ctx),0);

        }
        else {
            super.channelRead(ctx, msg);
        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isDebugEnabled()){
            log.debug("HeartbeatRequestHandler exceptionCaught");
        }
        super.exceptionCaught(ctx, cause);
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

            Channel channel  = ctx.channel();
            Integer failCount = maxHeartbeatFailCountRecords.get(channel.remoteAddress().toString());



            if (event.state()== IdleState.READER_IDLE){
               if(log.isWarnEnabled()){
                   log.warn("channel[{}]连接超时,关闭channel!" ,ctx.channel());
               }
                NettyContext.getNettyClient().connect(ctx.channel().remoteAddress());
                ChannelFuture channelFuture = ctx.channel().close();
                channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        Channel channel  = channelFuture.channel();

                        if(log.isDebugEnabled()){
                            log.debug("channel[{}]状态[{}]",channel.remoteAddress(),channel.isActive());
                        }
                    }
                });

            }
            else if (event.state()== IdleState.WRITER_IDLE){
                if(log.isDebugEnabled()){
                    log.debug("写空闲事件发生，向[{}]发送心跳数据",ctx.channel().remoteAddress());
                }
                NettyMessage nettyMessage = buildHeartbeatMessage();
                ctx.channel().writeAndFlush(nettyMessage);
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
        header.setType(MessageType.HEARTBEAT_REQUEST_TYPE.getValue());
        nettyMessage.setHeader(header);
        return  nettyMessage;
    }


    private String getAddress(ChannelHandlerContext ctx){
        return ctx.channel().remoteAddress().toString();
    }
}

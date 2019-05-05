package com.nz.rpc.netty.server.handler;

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


@Slf4j
public class HeartbeatResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;

        if( (message.getHeader() != null)
            &&(message.getHeader().getType() == MessageType.HEARTBEAT_REQUEST_TYPE)){
            if(log.isDebugEnabled()){
                log.debug("recv HeartbeatResponse [{}-{}]",ctx.channel().remoteAddress(),ctx.channel().id());
            }

            NettyMessage nettyMessage = new NettyMessage();
            Header header =  new Header();
            header.setType(MessageType.HEARTBEAT_RESPONSE_TYPE);
            nettyMessage.setHeader(header);

            ctx.channel().writeAndFlush(nettyMessage);
        }
        else {
            super.channelRead(ctx, msg);
        }

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {



        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(log.isDebugEnabled()){
                log.debug("userEventTriggered....[{}]",event.state());
            }

            if (event.state()== IdleState.READER_IDLE){
                if(log.isDebugEnabled()){
                    log.debug("close channel :{}" ,ctx.channel());
                }

                ChannelFuture channelFuture = ctx.channel().close();
                channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        Channel channel  = channelFuture.channel();
                        if(log.isDebugEnabled()){
                            log.info("channel[{}] state [{}]",channel.remoteAddress(),channel.isActive());
                        }

                    }
                });
            }
        }
        else {
            super.userEventTriggered(ctx, evt);
        }
    }

}

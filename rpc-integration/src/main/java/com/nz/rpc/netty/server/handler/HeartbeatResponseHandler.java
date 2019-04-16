package com.nz.rpc.netty.server.handler;

import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HeartbeatResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage)msg;

        if( (message.getHeader() != null)
            &&(message.getHeader().getType() == MessageType.HEARTBEAT_REQUEST_TYPE)){
            log.debug("接收到来自客户端[{}]的心跳消息",ctx.channel().remoteAddress());
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
}

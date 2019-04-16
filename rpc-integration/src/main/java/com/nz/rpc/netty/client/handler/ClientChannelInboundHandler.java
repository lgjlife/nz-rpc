package com.nz.rpc.netty.client.handler;


import com.nz.rpc.msg.MsgRequestHandler;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientChannelInboundHandler extends ChannelInboundHandlerAdapter {

    public ClientChannelInboundHandler() {
        super();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelRegistered　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelUnregistered　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelActive　,remoteAddress[{}],",ctx.channel().remoteAddress());
        NettyMessage nettyMessage = new NettyMessage();
        Header header =  new Header();
        header.setType(MessageType.APP_REQUEST_TYPE);
      //  nettyMessage.setBody("client request ");
        nettyMessage.setHeader(header);

        ctx.channel().writeAndFlush(nettyMessage);
       // super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelInactive　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("ServerChannelInboundHandler channelRead　,remoteAddress[{}],",ctx.channel().remoteAddress());
        log.debug("接收到服务端返回消息:"+msg);
        NettyMessage  nettyMessage = (NettyMessage)msg;
        MsgRequestHandler msgRequestHandler = MsgRequestHandler.getInstance();
        msgRequestHandler.finish(nettyMessage.getBody());
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelReadComplete　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("ServerChannelInboundHandler userEventTriggered　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelInboundHandler channelWritabilityChanged　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("ServerChannelInboundHandler exceptionCaught　,remoteAddress[{}],",ctx.channel().remoteAddress());
        super.exceptionCaught(ctx, cause);
    }
}

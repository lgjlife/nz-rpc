package com.nz.rpc.netty.server.handler;


import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.msg.ServerMessageHandler;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerChannelInboundHandler extends ChannelInboundHandlerAdapter {

    public ServerChannelInboundHandler() {
        super();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("与客户端[{}]建立连接",ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与客户端[{}]断开连接",ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("ServerChannelInboundHandler channelRead　,remoteAddress[{}],",ctx.channel().remoteAddress());
            log.debug("接收到客户端消息:"+msg);
        }
        NettyMessage nettyMessage =  (NettyMessage)  msg;
        if((nettyMessage!=null) && (nettyMessage.getHeader().getType() == MessageType.APP_REQUEST_TYPE.getValue())){

            ServerMessageHandler.getInstance().submit(ctx,(RpcRequest)nettyMessage.getBody());
        }

        super.channelRead(ctx, msg);
    }



}

package com.nz.rpc.netty.client.handler;


import com.nz.rpc.msg.ClientMessageHandler;
import com.nz.rpc.msg.RpcResponse;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 *功能描述 
 * @author lgj
 * @Description 客户端消息接收处理
 * @date 4/17/19
*/
@Slf4j
public class ClientChannelInboundHandler extends ChannelInboundHandlerAdapter {

    public ClientChannelInboundHandler() {
        super();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(log.isInfoEnabled()){
            log.info("Connect to server [{}] success!",ctx.channel().remoteAddress());
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(log.isWarnEnabled()){
            log.warn("Disconnect to server [{}]",ctx.channel().remoteAddress());
        }

        ctx.channel().close();
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //log.debug("ServerChannelInboundHandler channelRead　,remoteAddress[{}],",ctx.channel().remoteAddress());
        if (log.isDebugEnabled()){
            log.debug("recv data from [{}]/r/n[{}]",ctx.channel().remoteAddress(),msg);
        }

        NettyMessage  nettyMessage = (NettyMessage)msg;
        ClientMessageHandler clientMessageHandler = ClientMessageHandler.getInstance();

        clientMessageHandler.responseCallback((RpcResponse)nettyMessage.getBody());
        super.channelRead(ctx, msg);
    }


}

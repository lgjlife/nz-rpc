package com.nz.rpc.netty.client.handler;

import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class HeartbeatRequestHandler extends ChannelInboundHandlerAdapter {

    public HeartbeatRequestHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.executor().scheduleAtFixedRate(new HeartbeatRequestHandler.HeartbeatTask(ctx),0,5, TimeUnit.SECONDS);

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage)msg;
        if((message.getHeader() != null)
           &&(message.getHeader().getType() == MessageType.HEARTBEAT_RESPONSE_TYPE)){
            log.debug("接收到[{}]的心跳响应消息",ctx.channel().remoteAddress());
        }
       // super.channelRead(ctx, msg);


    }

    private  class   HeartbeatTask implements  Runnable{

        private  ChannelHandlerContext ctx;

        public HeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            log.debug("给[{}]发送心跳消息",ctx.channel().remoteAddress());
            NettyMessage nettyMessage = buildHeartbeatMessage();
            ctx.writeAndFlush(nettyMessage);
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
}

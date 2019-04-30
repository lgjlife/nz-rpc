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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("ServerChannelInboundHandler channelRead　,remoteAddress[{}],",ctx.channel().remoteAddress());
        log.debug("接收到客户端消息:"+msg);
       /* NettyMessage nettyMessage = (NettyMessage)msg;
        Header header =  new Header();
        header.setType(MessageType.APP_REQUEST_TYPE);
        nettyMessage.setBody("server response ");
        nettyMessage.setHeader(header);

        ctx.channel().writeAndFlush(nettyMessage);*/
        NettyMessage nettyMessage =  (NettyMessage)  msg;
        if((nettyMessage!=null) && (nettyMessage.getHeader().getType() == MessageType.APP_REQUEST_TYPE)){

            RpcRequest request = (RpcRequest)(nettyMessage.getBody());
            ServerMessageHandler.getInstance().submit(ctx,request);
     /*       NettyMessage  message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.APP_RESPONE_TYPE);
            nettyMessage.setHeader(header);
            RpcResponse response = new RpcResponse();
            response.setResponseId(request.getRequestId());
            response.setResult(new Random().nextInt(10000)+"");
            nettyMessage.setBody(response);

            ctx.writeAndFlush(nettyMessage);*/
        }

        super.channelRead(ctx, msg);
    }

}

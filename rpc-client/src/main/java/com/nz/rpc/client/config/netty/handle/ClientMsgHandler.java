package com.nz.rpc.client.config.netty.handle;

import com.nz.rpc.rpcsupport.utils.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientMsgHandler extends ChannelInboundHandlerAdapter {

    public ClientMsgHandler() {


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info("TimeClientHandler   exceptionCaught ");
        ctx.close();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //  super.channelActive(ctx);
        log.info("连接成功！");

        RpcRequest request = new RpcRequest();
        request.setClassName("com.nz.rpc.rpcserver.service.impl.IDemo1Service");
        request.setMethodName("func4");
        String[] as = new String[1];
        as[0] = "asaddsasad";
        Class<?>[] types = new Class[1];
        types[0] = as[0].getClass();
        request.setParameterTypes(types);
        request.setParameters(as);

        ctx.writeAndFlush(request);
        //ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //  super.channelRead(ctx, msg);
        log.info("TimeClientHandler   channelRead ");
        log.info("正在读取来自服务端的数据.........");
        log.info("接受的数据为：" + msg);

        // ctx.close();

    }
}

package com.nz.rpc.client.config.netty.handle;

import com.nz.rpc.rpcsupport.utils.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    public ClientInboundHandler() {


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
        String  cla =  "app.provider.service.UserService";
        //String  cla = "com.app.common.controller.IUserService";
        request.setClassName(cla);
        request.setMethodName("queryName");
        Long[] as = new Long[1];
        as[0] = 123L;
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

package com.nz.rpc.rpcserver.netty.server.handle;

import com.nz.rpc.rpcserver.handle.requret.ClientRequestHandler;
import com.nz.rpc.rpcserver.netty.server.utils.NettyClientConnectUtil;
import com.nz.rpc.rpcsupport.utils.RpcRequest;
import com.nz.rpc.rpcsupport.utils.RpcResponse;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@Slf4j
public class MsgServerHandler extends ChannelHandlerAdapter {

    private AtomicInteger counter = new AtomicInteger(0);


    ClientRequestHandler clientRequestHandler = new ClientRequestHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        log.info("MsgServerHandler  exceptionCaught:" + cause.getMessage());
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);


        log.info("连接成功！");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("channelRegistered...");
        NettyClientConnectUtil.addConnectCounter();
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.close(ctx, promise);
        log.info("close...");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        NettyClientConnectUtil.decConnectCounter();
        log.info("handlerRemoved...");

    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {

        NettyClientConnectUtil.addConnectCounter();

        log.info("connect...");
        log.info("remoteAddress = " + remoteAddress.toString());
        log.info("localAddress = " + localAddress.toString());
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        // super.disconnect(ctx, promise);
        NettyClientConnectUtil.decConnectCounter();
        log.info("disconnect...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("正在读取来自客户端的数据.........");
        //TestUser user1 = (TestUser) msg;
        log.info("客户端的数据：" + msg);
        log.debug("clientRequestHandler is null ? " + (clientRequestHandler == null));
        RpcResponse response = clientRequestHandler.handler((RpcRequest) msg);
        log.debug("response = " + response);

        // TestUser user = new TestUser("我来自服务端",18,222);
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //123456super.channelReadComplete(ctx);
        log.info("TimeServerHandler  channelReadComplete");
        ctx.flush();
    }
}

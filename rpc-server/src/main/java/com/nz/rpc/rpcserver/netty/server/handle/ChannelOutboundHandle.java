package com.nz.rpc.rpcserver.netty.server.handle;

import com.nz.rpc.rpcserver.handle.requret.ClientRequestHandler;
import com.nz.rpc.rpcserver.netty.server.utils.NettyClientConnectUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@Slf4j
public class ChannelOutboundHandle extends ChannelOutboundHandlerAdapter {

    private AtomicInteger counter = new AtomicInteger(0);


    ClientRequestHandler clientRequestHandler = new ClientRequestHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
        log.info("MsgServerHandler  exceptionCaught:" + cause.getMessage());
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




}

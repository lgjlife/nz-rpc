package com.nz.rpc.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;


/**
 *功能描述
 * @author lgj
 * @Description  连接相关的handle
 * @date 3/27/19
*/

@Slf4j
public class ClientChannelOutboundHandle extends ChannelOutboundHandlerAdapter {

    public ClientChannelOutboundHandle() {
        super();
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle bind　,remoteAddress[{}]",ctx.channel().remoteAddress());
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle connect　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle disconnect　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle close　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.close(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle deregister　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.deregister(ctx, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelOutboundHandle read　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.debug("ServerChannelOutboundHandle write　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        log.debug("ServerChannelOutboundHandle flush　,remoteAddress[{}]",ctx.channel().remoteAddress());

        super.flush(ctx);
    }
}

package com.nz.rpc.rpcserver.netty.server.handle;


import com.nz.rpc.rpcsupport.netty.config.CoderConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        CoderConfig.JdkCoder(socketChannel);
        socketChannel.pipeline().addLast(new MsgServerHandler());
    }
}

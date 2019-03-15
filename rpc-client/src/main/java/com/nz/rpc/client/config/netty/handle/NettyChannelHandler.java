package com.nz.rpc.client.config.netty.handle;


import com.nz.rpc.rpcsupport.netty.config.CoderConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        CoderConfig.JdkCoder(socketChannel);
        socketChannel.pipeline().addLast(new ClientMsgHandler());
    }
}

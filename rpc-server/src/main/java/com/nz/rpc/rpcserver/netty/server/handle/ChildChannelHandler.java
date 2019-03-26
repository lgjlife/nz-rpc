package com.nz.rpc.rpcserver.netty.server.handle;


import com.nz.rpc.rpcsupport.netty.config.CoderConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 *功能描述 
 * @author lgj
 * @Description   配置责任链 handler
 * @date 3/16/19
*/
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ChannelInboundHandle());
        pipeline.addLast(new ChannelOutboundHandle());
    }
}

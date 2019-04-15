package com.nz.rpc.netty.client.handler;


import com.nz.rpc.netty.coder.MsgCoder;
import com.nz.rpc.netty.coder.MsgDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 *功能描述
 * @author lgj
 * @Description  注册handler 拦截器
 * @date 3/15/19
*/
public class NettyChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      //  CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new MsgDecoder());
        pipeline.addLast(new MsgCoder());
        pipeline.addLast(new ClientChannelInboundHandler());
        pipeline.addLast(new ClientChannelOutboundHandle());
    }
}

package com.nz.rpc.netty.server.handler;


import com.nz.rpc.netty.coder.NettyMessageDecode;
import com.nz.rpc.netty.coder.NettyMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *功能描述 
 * @author lgj
 * @Description   配置责任链 handler
 * @date 3/16/19
*/
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new NettyMessageDecode(1024*1024,9, 4));

        pipeline.addLast(new IdleStateHandler(75, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatResponseHandler());


        pipeline.addLast(new NettyMessageEncoder());

        pipeline.addLast(new ServerChannelInboundHandler());
     //   pipeline.addLast(new ServerChannelOutboundHandle());

    }
}

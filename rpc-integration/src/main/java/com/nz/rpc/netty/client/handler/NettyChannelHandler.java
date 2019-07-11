package com.nz.rpc.netty.client.handler;


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
 * @Description  注册handler 拦截器
 * @date 3/15/19
*/

public class NettyChannelHandler extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      //  CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();



        pipeline.addLast(new NettyMessageDecode(1024*1024,9,4));

        pipeline.addLast(new IdleStateHandler(25, 25, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatRequestHandler());


        pipeline.addLast(new NettyMessageEncoder());

        pipeline.addLast(new ClientChannelInboundHandler());


    }
}

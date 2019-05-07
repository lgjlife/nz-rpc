package com.nz.rpc.netty.client.handler;


import com.nz.rpc.netty.coder.MsgCoder;
import com.nz.rpc.netty.coder.MsgDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 *功能描述
 * @author lgj
 * @Description  注册handler 拦截器
 * @date 3/15/19
*/
@Data
public class NettyChannelHandler extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      //  CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        pipeline.addLast(new MsgDecoder());
        pipeline.addLast(new LengthFieldPrepender(2));
        pipeline.addLast(new MsgCoder());
        pipeline.addLast(new IdleStateHandler(5, 5, 0, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatRequestHandler());
        pipeline.addLast(new ClientChannelInboundHandler());
        pipeline.addLast(new ClientChannelOutboundHandle());

    }
}

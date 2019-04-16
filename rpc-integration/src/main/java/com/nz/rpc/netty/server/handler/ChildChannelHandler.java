package com.nz.rpc.netty.server.handler;


import com.nz.rpc.netty.coder.MsgCoder;
import com.nz.rpc.netty.coder.MsgDecoder;
import com.nz.rpc.netty.server.NettyServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 *功能描述 
 * @author lgj
 * @Description   配置责任链 handler
 * @date 3/16/19
*/
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    private NettyServer nettyServer;

    public ChildChannelHandler(NettyServer nettyServer) {
        this.nettyServer = nettyServer;
    }



    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //CoderConfig.JdkCoder(socketChannel);
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        pipeline.addLast(new MsgDecoder(nettyServer.getSerialize()));
        pipeline.addLast(new LengthFieldPrepender(2));
        pipeline.addLast(new MsgCoder(nettyServer.getSerialize()));
        pipeline.addLast(new HeartbeatResponseHandler());
        pipeline.addLast(new ServerChannelInboundHandler());
        pipeline.addLast(new ServerChannelOutboundHandle());
    }
}

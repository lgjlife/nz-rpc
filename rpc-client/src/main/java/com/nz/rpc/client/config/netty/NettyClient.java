package com.nz.rpc.client.config.netty;

import com.nz.rpc.client.config.netty.handle.NettyChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyClient {

    private  EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();


    public NettyClient(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyChannelHandler());

    }


    public void connect(String host,int port){

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //  future.channel().closeFuture().sync();
            //   future.channel().close().sync();


        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}

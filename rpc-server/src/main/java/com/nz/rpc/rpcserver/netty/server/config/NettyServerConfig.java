package com.nz.rpc.rpcserver.netty.server.config;


import com.nz.rpc.rpcserver.netty.server.handle.ChildChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service

@Slf4j


@Component
public class NettyServerConfig implements ApplicationContextAware {

    public void bind(int port) throws Exception {

        log.info("service bind port = " + port);
        //bossGroup接受传入的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //一旦bossGroup接受连接并注册到workerGroup，workerGroup则处理连接相关的流量
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //用于设置服务端
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());

            //绑定端口，同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            Channel channel = channelFuture.channel();

            log.info("绑定端口，同步等待成功");
            //等待服务端监听端口关闭
            //   channelFuture.channel().close().sync();
            log.info("等待服务端监听端口关闭");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("shutdownGracefully....");
            //  bossGroup.shutdownGracefully();
            //  workerGroup.shutdownGracefully();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            this.bind(8112);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}

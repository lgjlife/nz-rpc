package com.nz.rpc.netty.server;

import com.nz.rpc.netty.server.handler.ChildChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyServer {

    public void bind(int port) {

        log.info("controller bind port = " + port);
        //reactor 主从模式 EventLoopGroup 线程池
        //bossGroup 用于安全认证，登录，握手，一但3链路建立成功，就将链路注册到workerGroup线程上
        //后续有其处理IO操作
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
            log.debug("正在绑定端口:[{}]",port);
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            Channel channel = channelFuture.channel();

            channelFuture.addListener(new GenericFutureListener(){
                @Override
                public void operationComplete(Future future) throws Exception {
                    log.debug("绑定端口:[{}]成功,channel状态[{}]",port,channel.isActive());

                }
            });
          //  log.info("绑定端口，同步等待成功");
            //等待服务端监听端口关闭
            //   channelFuture.channel().close().sync();
         //   log.info("等待服务端监听端口关闭");

        } catch (Exception ex) {
            log.error("服务端绑定端口[{}]失败！",port,ex);
        } finally {
            log.info("shutdownGracefully....");
            //  bossGroup.shutdownGracefully();
            //  workerGroup.shutdownGracefully();
        }
    }
}

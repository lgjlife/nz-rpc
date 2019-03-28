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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Slf4j
@Configuration
@EnableConfigurationProperties
public class NettyServerConfig   {

    private  static final int  port = 8112;

    public void bind(int port) throws Exception {

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

    @PostConstruct
    public void  register(){
        try{
            bind(port);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /*@Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/
}

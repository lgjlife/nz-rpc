package com.nz.rpc.netty.server;

import com.nz.rpc.netty.server.handler.ChildChannelHandler;
import com.nz.rpc.properties.RpcProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class NettyServer {


    //bossGroup接受传入的连接
    EventLoopGroup bossGroup;
    //一旦bossGroup接受连接并注册到workerGroup，workerGroup则处理连接相关的流量
    EventLoopGroup workerGroup;
    RpcProperties rpcProperties;

    public NettyServer(RpcProperties rpcProperties) {
        this.rpcProperties = rpcProperties;
        //bossGroup接受传入的连接
        bossGroup = new NioEventLoopGroup(rpcProperties.getNettyServer().getBossTheads(),
                new DefaultThreadFactory("server1", true));
        //一旦bossGroup接受连接并注册到workerGroup，workerGroup则处理连接相关的流量
        workerGroup = new NioEventLoopGroup(rpcProperties.getNettyServer().getWorkerTheads(),
                new DefaultThreadFactory("server2", true));


    }

    public void bind(int port) {

        log.debug("Server bind to port[{}]",port);
        //reactor 主从模式 EventLoopGroup 线程池
        //bossGroup 用于安全认证，登录，握手，一但3链路建立成功，就将链路注册到workerGroup线程上
        //后续有其处理IO操作

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //用于设置服务端
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //.option(ChannelOption.TCP_NODELAY, false)
                    //.option(ChannelOption.SO_SNDBUF,1024*1024)
                    .option(ChannelOption.SO_RCVBUF,1024*1024)
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            log.debug("Binding the port[{}].....",port);
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            Channel channel = channelFuture.channel();

            channelFuture.addListener(new GenericFutureListener<ChannelFuture>(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Channel channel = future.channel();
                    if(channel.isActive()){
                        log.debug("Binding the port:[{}] successs,channel state [{}]",port,channel.isActive());
                    }
                    else {
                        log.debug("Binding the port:[{}]fail,channel state [{}]",port,channel.isActive());
                        System.exit(1);
                    }

                }
            });


        } catch (Exception ex) {
            log.error("Binding the port:[{}]fail,ex={}！",port,ex);
            System.exit(1);
        } finally {
            log.info("shutdownGracefully....");
            //  bossGroup.shutdownGracefully();
            //  workerGroup.shutdownGracefully();
        }
    }
}

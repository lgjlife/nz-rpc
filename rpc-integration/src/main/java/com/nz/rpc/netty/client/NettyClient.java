package com.nz.rpc.netty.client;

import com.nz.rpc.netty.client.handler.NettyChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private static  Map<String, Channel> channelCache = new ConcurrentHashMap<>();


    public NettyClient(){
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyChannelHandler());

    }


    public void connect(String host,int port){

        try {
            log.debug("与服务端[{}:{}]进行连接.....",host,port);

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.addListener(new GenericFutureListener(){
                @Override
                public void operationComplete(Future future) throws Exception {
                    log.debug("与服务端[{}:{}]连接状态！连接状态:[{}]",host,port,future.isSuccess());
                    NettyClient.channelCache.put(getKey(host,port),channelFuture.channel());
                }

            } );

        } catch (Exception ex) {
            log.debug("与服务端[{}:{}]连接失败！发起重连！",host,port);
            try{
                Thread.sleep(100);
            }
            catch(Exception e){
                log.error(e.getMessage());
            }
            this.connect(host,port);
        }

    }

    private  String getKey(String host,int port){

        return  new StringBuilder().append(host).append(":").append(port).toString();
    }

}

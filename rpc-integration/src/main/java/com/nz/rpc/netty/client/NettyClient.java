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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap ;
    private static  Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    private static final  int reConnectIntervalTimeMs = 5000;





    public NettyClient(){
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyChannelHandler());

    }

    public void  connect(SocketAddress remoteAddress){

        connect(parseToHost(remoteAddress),parseToPort(remoteAddress));
    }

    public void connect(String host,int port){

        try {
            log.debug("与服务端[{}:{}]进行连接.....",host,port);

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.addListener(new GenericFutureListener(){
                @Override
                public void operationComplete(Future future) throws Exception {
                    log.debug("与服务端[{}:{}]连接状态！连接状态:[{}]",host,port,future.isSuccess());
                    if(future.isSuccess() == false){
                        channelCache.remove(getKey(host,port));
                        new Thread(){
                            @Override
                            public void run() {
                                try{
                                    Thread.sleep(reConnectIntervalTimeMs);
                                    connect(host,port);
                                }
                                catch(Exception ex){
                                    log.error(ex.getMessage());
                                }
                            }
                        }.start();
                    }
                    else {
                        channelCache.put(getKey(host,port),channelFuture.channel());
                    }

                }

            } );

        } catch (Exception ex) {
            log.debug("与服务端[{}:{}]连接失败！发起重连！",host,port);
        }

    }

    public Channel getChannel(String host,int port){

       return channelCache.get(host + ":" + port);
    }

    private String  parseToHost(SocketAddress remoteAddress){
        String address = remoteAddress.toString();

        int end = address.indexOf(":");

        return address.substring(1,end);
    }
    private int  parseToPort(SocketAddress remoteAddress){
        String address = remoteAddress.toString();

        int start = address.indexOf(":");

        return Integer.valueOf( address.substring(start+1,address.length()));

    }



    private  String getKey(String host,int port){

        return  new StringBuilder().append(host).append(":").append(port).toString();
    }

}

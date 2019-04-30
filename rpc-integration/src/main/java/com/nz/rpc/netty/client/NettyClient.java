package com.nz.rpc.netty.client;

import com.nz.rpc.netty.client.handler.NettyChannelHandler;
import com.nz.rpc.properties.RpcProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
public class NettyClient {

    private EventLoopGroup group ;
    private Bootstrap bootstrap ;
    private static  Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    private static final  int reConnectIntervalTimeMs = 5000;

    private List<String> connectingServer = new CopyOnWriteArrayList<String>();




    public NettyClient(RpcProperties rpcProperties){
        group = new NioEventLoopGroup(rpcProperties.getNettyClient().getWorkerTheads(),
                new DefaultThreadFactory("client1", true));
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

        String key = getKey(host,port);

        if(connectingServer.contains(key)){
            return;
        }
        connectingServer.add(key);

        try {
            log.debug("conect to [{}:{}].....",host,port);

            ChannelFuture channelFuture = bootstrap.connect(host, port);

            channelFuture.addListener(new GenericFutureListener(){
                @Override
                public void operationComplete(Future future) throws Exception {
                    log.debug("connect to  [{}:{}] state [{}]",host,port,future.isSuccess());
                    if(future.isSuccess() == false){
                        channelCache.remove(key);
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
                        channelCache.put(key,channelFuture.channel());
                        connectingServer.remove(key);
                    }

                }

            } );

        } catch (Exception ex) {
            log.debug("connect to [{}:{}] fail , reconnect again !",host,port);
        }

    }

    public Channel getChannel(String host,int port){
        Channel channel = channelCache.get(host + ":" + port);
        log.debug("channelCache len =[{}]",channelCache.size());

        if(channel == null){
            this.connect(host,port);
        }

        return channelCache.get(host + ":" + port);
    }

    public void removeChannel(String host,int port){
        String key = getKey(host,port);

        Channel channel =  channelCache.get(key);
        if(channel != null){
            channelCache.remove(host + ":" + port);
            channel.close();
        }

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

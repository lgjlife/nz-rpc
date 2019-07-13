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
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
public class NettyClient {

    private EventLoopGroup group ;
    private Bootstrap bootstrap ;
    private static  Map<String, Channel> channelCache = new ConcurrentHashMap<>();

    private List<String> connectingAddress = new CopyOnWriteArrayList<>();


    //重连间隔时间
    private static final  int reConnectIntervalTimeMs = 5000;

    private Set<String> connectingServer = new ConcurrentSkipListSet<String>();

    private RpcProperties rpcProperties;


    public NettyClient(RpcProperties rpcProperties){

        this.rpcProperties =  rpcProperties;



    }

    public void init(){

        group = new NioEventLoopGroup(rpcProperties.getNettyClient().getWorkerTheads(),
                new DefaultThreadFactory("client1", true));
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.SO_SNDBUF,1024*1024)
                .option(ChannelOption.SO_RCVBUF,1024*1024)
                .handler(new NettyChannelHandler());
    }

    public void  connect(SocketAddress remoteAddress){

        connect(parseToHost(remoteAddress),parseToPort(remoteAddress));
    }

    public void connect(String host,int port){

        if((rpcProperties.getNhost().equals(host)) && (rpcProperties.getNport() == port)){
            log.warn("Cannot  connect to youself!");
            return;
        }

        String key = getKey(host,port);

        synchronized (connectingAddress){
            Channel channel = channelCache.get(key);

            //host$port正在连接状态
            if(connectingAddress.contains(key)){
                log.warn("Serere [{}:{}] is connecting",host,port);
                return;

            }
            else if(channel != null){
                //host$port连接成功状态
                log.debug("Channel state isActive= [{}],isOpen= [{}],isRegistered= [{}],",
                        channel.isActive(),channel.isOpen(),channel.isRegistered());
                if(channel.isActive()){
                    log.debug("Channel[{}] has been active",channel);
                    return;
                }


            }
             {
                log.debug("Connecting to Serere [{}:{}]",host,port);
                connectingAddress.add(key);
                bootstrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>(){

                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        Channel channel = channelFuture.channel();

                        if(channel.isActive()){
                            log.debug("Server[{}] connect success",channel);
                            connectingAddress.remove(key);
                            channelCache.put(key,channel);
                            return;
                        }else {
                            log.warn("Connecting to Serere [{}:{}] fail!!,Try reconect!!",host,port);
                            connectingAddress.remove(key);
                            Thread.sleep(reConnectIntervalTimeMs);
                            connect(host, port);
                        }
                    }
                });
            }
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

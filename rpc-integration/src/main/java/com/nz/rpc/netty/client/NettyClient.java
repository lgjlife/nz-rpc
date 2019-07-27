package com.nz.rpc.netty.client;

import com.nz.rpc.netty.client.handler.NettyChannelHandler;
import com.nz.rpc.properties.RpcProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *功能描述 
 * @author lgj
 * @Description  nertty操作客户端
 * @date 7/14/19
*/
@Slf4j
@Data
public class NettyClient {

    private EventLoopGroup group ;
    private Bootstrap bootstrap ;

    //channel缓存 key : host&port
    private static  Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    //重连间隔时间
    private static final  int reConnectIntervalTimeMs = 5000;
    //value: host&port 正在连接状态的Server  host&port
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
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .handler(new NettyChannelHandler());

        shutdownHandler();
    }

    public void  connect(SocketAddress remoteAddress){

        connect(parseToHost(remoteAddress),parseToPort(remoteAddress));
    }

    /**
     *功能描述 
     * @author lgj
     * @Description 连接到端口
     * @date 7/13/19
     * @param: 
     * @return:  
     *
    */
    public void connect(String host,int port){

        if(host == null){
            throw  new NullPointerException("Host["+host+"] is null,Connect fail!");
        }

        //如果和本应用的host&port一致，则拒绝执行连接
        if((rpcProperties.getNhost().equals(host)) && (rpcProperties.getNport() == port)){
            log.warn("Netty cann't  connect to youself!");
            return;
        }

        String key = getKey(host,port);

        synchronized (connectingServer){
            Channel channel = channelCache.get(key);
            //host&port正在连接状态
            if(connectingServer.contains(key)){
                if(log.isInfoEnabled()){
                    log.warn("Serere [{}:{}] is connecting",host,port);
                }
                return;

            }
            else if(channel != null){
                //host&port连接成功状态
                if(log.isDebugEnabled()){
                    log.debug("Channel state isActive= [{}],isOpen= [{}],isRegistered= [{}]",
                            channel.isActive(),channel.isOpen(),channel.isRegistered());
                }
                if(channel.isActive()){
                    if(log.isDebugEnabled()){
                        log.debug("Channel[{}] has been active！Don't neet to connect!",channel);
                    }
                    return;
                }
            }
            if(log.isDebugEnabled()){
                log.debug("Connecting to Serere [{}:{}]",host,port);
            }
            connectingServer.add(key);
            bootstrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>(){
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    Channel channel = channelFuture.channel();
                    //log.info("connectingServer = " + connectingServer);
                    if(channel.isActive()){
                        if(log.isDebugEnabled()){
                            log.debug("Server[{}] connect success",channel);
                        }
                        connectingServer.remove(key);
                        channelCache.put(key,channel);
                        return;
                    }else {
                        if(log.isWarnEnabled()){
                            log.warn("Connecting to Serere [{}:{}] fail!!,Try reconect!!",host,port);
                        }
                        Thread.sleep(reConnectIntervalTimeMs);
                        connectingServer.remove(key);
                        connect(host, port);
                    }
                }
            });

        }
    }



    public Channel getChannel(String host,int port){
        Channel channel = channelCache.get(getKey(host,port));
        if(log.isDebugEnabled()){
            log.debug("channelCache len =[{}]",channelCache.size());
        }

        if(channel == null){
            this.connect(host,port);
        }

        return channelCache.get(getKey(host,port));
    }

    public void removeChannel(String host,int port){
        String key = getKey(host,port);

        Channel channel =  channelCache.get(key);
        if(channel != null){
            channelCache.remove(getKey(host,port));
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

        return  new StringBuilder().append(host).append("&").append(port).toString();
    }

    /*
     *功能描述 应用关闭处理
     * @author lgj
     * @Description
     * @date 7/26/19
     * @param:
     * @return:  void
     *
     */
    private void shutdownHandler(){

        new Thread(){

            @Override
            public void run() {
                Runtime.getRuntime().addShutdownHook(new Thread(){

                    @Override
                    public void run() {
                        if(log.isInfoEnabled()){
                            log.info("系统Netty-Client正在关闭应用！");
                        }
                        group.shutdownGracefully();
                    }
                });

            }
        }.start();
    }

}

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

import java.net.InetAddress;
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

    //重连间隔时间
    private static final  int reConnectIntervalTimeMs = 5000;

    private List<String> connectingServer = new CopyOnWriteArrayList<String>();

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
    public void  connect(InetAddress inetHost, int inetPort){

    }

    /**
     *功能描述 
     * @author lgj
     * @Description  连接到端口
     * @date 7/12/19
     * @param:  
     * 
     * @return: 
     * 
     *
    */
    public void connect(String host,int port){

        String key = getKey(host,port);

        synchronized (channelCache){
            Channel channel = channelCache.get(key);
            if(channel != null){
                log.debug("Channel[{}] has been active",channel);
                if((channel.isActive())){

                    return;
                }
            }
            //channel 不存在或者 channel处于激活状态，则创建连接

            log.debug("conect to [{}:{}].....",host,port);
             bootstrap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>(){
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.debug("connect to  [{}:{}] state [{}]",host,port,channelFuture.isSuccess());

                    Channel channel = channelFuture.channel();
                    if(channel.isActive()){
                        channelCache.put(key,channelFuture.channel());
                    }
                    else {
                        log.warn("Can not to connect to Server[{}:{}]!",host,port);
                        new Thread(){
                            @Override
                            public void run() {
                            try{
                                Thread.sleep(reConnectIntervalTimeMs);
                                String key = getKey(host,port);
                                Channel channel = channelCache.get(key);
                                if((channel != null)&&(channel.isActive())){
                                    log.debug("Channel[{}] has been active",channel);
                                    return;
                                }
                                connect(host,port);
                            }
                            catch(Exception ex){
                                log.error("Conect to [{}:{}] Exception:{}",host,port,ex);
                                ex.printStackTrace();
                            }
                            }
                        }.start();

                    }
                }

            } );
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

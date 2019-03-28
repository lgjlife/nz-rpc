package com.nz.rpc.client.config.netty;

import com.nz.rpc.client.config.ServiceConfig;
import com.nz.rpc.client.config.netty.handle.NettyChannelHandler;
import com.nz.rpc.rpcsupport.utils.RegistryConfig;
import com.nz.rpc.rpcsupport.utils.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *功能描述 
 * @author lgj
 * @Description  netty 初始配置，连接
 * @date 3/28/19
*/
@Slf4j
public class NettyClient {

    private  EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap bootstrap = new Bootstrap();
    private Map<String, Channel>  ChannelCache = new ConcurrentHashMap<>();


    public NettyClient(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new NettyChannelHandler());

    }


    public Channel connect(String host,int port){

        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            return channel;

            //  future.channel().closeFuture().sync();
            //  future.channel().close().sync();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    public void sendData(RpcRequest request){

        String className = request.getClassName();

        List<RegistryConfig> services = ServiceConfig.getServices().get(className);

        if(ChannelCache.get(className) == null){
           // this.connect()
        }
    }
}

package com.nz.rpc.client.config.netty;


import com.nz.rpc.client.config.netty.handle.NettyChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 *功能描述
 * @author lgj
 * @Description   netty 主配置
 * @date 3/16/19
*/
@Configuration
@Slf4j
public class NettyClientConfig  {

    public void connect(String host, int port) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();


            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyChannelHandler());

            ChannelFuture future = bootstrap.connect(host, port).sync();


            //  future.channel().closeFuture().sync();
            //   future.channel().close().sync();


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }


    }

    @PostConstruct
    public  void init(){
        try {
            this.connect("127.0.0.1", 8112);
            log.debug("client connect to 127.0.0.1:8112 ");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

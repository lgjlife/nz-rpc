package com.nz.rpc.netty.client;

import com.nz.rpc.properties.RpcProperties;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class NettyClientHandle {

    private NettyClient nettyClient;
    private RpcProperties properties;


    public void connect(String host,int port){

        log.debug("正在连接到[{}]:[{}]",host,port);
        ChannelFuture channelFuture = nettyClient.getBootstrap().connect(host,port);
        channelFuture.addListener(new GenericFutureListener(){
            @Override
            public void operationComplete(Future future) throws Exception {

                log.debug("与服务端[{}:{}]连接状态！连接状态:[{}]",host,port,future.isSuccess());
                if(future.isSuccess() == false){
                    connect(host,port);
                }
            }
        });

    }
}

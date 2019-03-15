package com.nz.rpc.rpcsupport.netty.config;

import com.nz.rpc.rpcsupport.netty.coder.JdkDecoder;
import com.nz.rpc.rpcsupport.netty.coder.JdkEncoder;
import com.nz.rpc.rpcsupport.netty.coder.JsonDecoder;
import com.nz.rpc.rpcsupport.netty.coder.JsonEncoder;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class CoderConfig {

  /*  public static  void MsgpackCoder(SocketChannel socketChannel){
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
        socketChannel.pipeline().addLast(new MsgpackDecoder());
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
        socketChannel.pipeline().addLast(new MsgpackEncoder());
    }*/

    public static void JdkCoder(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        socketChannel.pipeline().addLast(new JdkDecoder());
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
        socketChannel.pipeline().addLast(new JdkEncoder());
    }

    public static void JsonCoder(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        socketChannel.pipeline().addLast(new JsonDecoder());
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
        socketChannel.pipeline().addLast(new JsonEncoder());
    }

}

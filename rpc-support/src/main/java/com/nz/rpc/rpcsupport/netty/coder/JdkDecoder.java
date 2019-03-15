package com.nz.rpc.rpcsupport.netty.coder;

import com.nz.rpc.rpcsupport.serialize.JdkSerialize;
import com.nz.rpc.rpcsupport.serialize.Serialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class JdkDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf o, List list) throws Exception {


        log.info("JdkDecoder decode....");
        byte[] b = new byte[o.readableBytes()];
        o.readBytes(b);
        Serialize serialize = new JdkSerialize();
        Object obj = serialize.deserialize(b);
        list.add(obj);
    }
}

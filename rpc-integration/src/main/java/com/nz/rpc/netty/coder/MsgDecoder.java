package com.nz.rpc.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class MsgDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        log.debug("decode....");
        byte[] b = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b);


        String str = new String(b);
        log.debug("str = " + str);
        list.add(str);


    }
}

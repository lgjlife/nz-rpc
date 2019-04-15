package com.nz.rpc.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgCoder extends MessageToByteEncoder {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        log.debug("encode....");
        String object = (String)o;
        byte[] b = object.getBytes();
        byteBuf.writeBytes(b);

    }
}

package com.nz.rpc.netty.coder;

import com.nz.rpc.serialization.AbstractSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgCoder extends MessageToByteEncoder<Object> {

    private AbstractSerialize serialize ;

    public MsgCoder(AbstractSerialize serialize) {
        this.serialize = serialize;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        log.debug("encode....");
        log.debug("向[{}]发送消息[{}]",channelHandlerContext.channel().remoteAddress(),o);
        byte[] b = serialize.serialize(o);
        log.debug("消息长度:{}",b.length);
        byteBuf.writeBytes(b);

    }
}

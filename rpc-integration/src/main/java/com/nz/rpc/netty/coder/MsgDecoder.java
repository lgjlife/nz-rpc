package com.nz.rpc.netty.coder;

import com.nz.rpc.netty.message.NettyMessage;
import com.nz.rpc.serialization.AbstractSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class MsgDecoder extends MessageToMessageDecoder<ByteBuf> {

    private AbstractSerialize serialize;

    public MsgDecoder(AbstractSerialize serialize) {
        this.serialize = serialize;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        log.debug("decode....");
        byte[] b = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(b);

        NettyMessage message =  serialize.deserialize(b, NettyMessage.class);
        log.debug("message = [{}]",message);
        list.add(message);


    }
}

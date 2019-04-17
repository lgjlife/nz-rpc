package com.nz.rpc.netty.coder;

import com.nz.rpc.netty.NettyContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgCoder extends MessageToByteEncoder<Object> {



    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {

        log.debug("向[{}]发送消息[{}]",channelHandlerContext.channel().remoteAddress(),o);
        byte[] b = NettyContext.getSerialize().serialize(o);
        byteBuf.writeBytes(b);

    }
}

package com.nz.rpc.netty.coder;

import com.nz.rpc.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage, List<Object> out) throws Exception {

        log.debug("+++++++++++++++++++++++encode  start +++++++++++++++++++++++");
        if( (nettyMessage == null) || (nettyMessage.getHeader() == null)){

            throw  new NullPointerException();
        }
        ByteBuf byteBuf =  Unpooled.buffer();// new PooledByteBufAllocator().directBuffer();//
        log.info("NettyMessageEncoder 发送解析 ： nettyMessage = " + nettyMessage);

        byteBuf.writeByte(nettyMessage.getHeader().getType());
        byteBuf.writeLong(nettyMessage.getHeader().getSessionID());
        byteBuf.writeInt(nettyMessage.getHeader().getLength());
        byteBuf.writeInt(nettyMessage.getHeader().getCrcCode());


        byteBuf =  MessageBodyUtil.encoder(nettyMessage.getBody(),byteBuf);

        //回写byteBuf总共长度
        log.info("数据总长度="+byteBuf.readableBytes());
        byteBuf.setInt(9,byteBuf.readableBytes());

        out.add(byteBuf);

        log.debug("==================encode end==================");

    }
}

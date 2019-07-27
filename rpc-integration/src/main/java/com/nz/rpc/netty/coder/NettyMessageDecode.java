package com.nz.rpc.netty.coder;


import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.msg.RpcResponse;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import com.nz.rpc.time.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyMessageDecode extends LengthFieldBasedFrameDecoder {

    public NettyMessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }




    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {

       // log.debug("×××××××××××××××××××××××decode  start×××××××××××××××××××××××");
       // log.info("maxCapacity = " + byteBuf.maxCapacity());

        if(byteBuf == null){

       //     log.info("byteBuf is null!");
            return null;
        }
        log.debug("readerIndex={},writerIndex={},readableByte={}",
                byteBuf.readerIndex(),byteBuf.writerIndex(),byteBuf.readableBytes());
        if(byteBuf.readableBytes() > (MessageIndex.lengthIndex+4)){

            int lengthIndex = byteBuf.readerIndex()+MessageIndex.lengthIndex;
           // if(log.isDebugEnabled()){log.info("lengthIndex = {}",lengthIndex);
            int messageLength = byteBuf.getInt(lengthIndex);
          //  log.debug("readerIndex={},writerIndex={},readableByte={},messageLength={}",
          //          byteBuf.readerIndex(),byteBuf.writerIndex(),byteBuf.readableBytes(),messageLength);


           if( byteBuf.readableBytes() < messageLength ){

               if(log.isDebugEnabled()){
                   log.debug("readableBytes is not enougth to be a data packege++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",byteBuf.readableBytes() );
               }
                return null;
            }

        }
        else {
            if(log.isDebugEnabled()){
                log.debug("readableBytes is not enougth to be a data packege++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++",byteBuf.readableBytes() );
            }
           return null;
        }

        NettyMessage nettyMessage = new NettyMessage();

        Header header = new Header();

        header.setType(byteBuf.readByte());
        header.setSessionID(byteBuf.readLong());
        header.setLength(byteBuf.readInt());

        if(log.isInfoEnabled()){
            log.info("本次接收的总长度为:[{}]byte",header.getLength());
        }
        header.setCrcCode(byteBuf.readInt());

        nettyMessage.setHeader(header);

        //rpc请求body解析
        if(header.getType() == MessageType.APP_REQUEST_TYPE.getValue()){
            RpcRequest request =  MessageBodyUtil.decoder(byteBuf, RpcRequest.class);
            TimeUtil.start("RequestHandler-run",request.getRequestId());
            nettyMessage.setBody(request);
        }
        //rpc响应body解析
        else if(header.getType() == MessageType.APP_RESPONE_TYPE.getValue()){
            RpcResponse response =  MessageBodyUtil.decoder(byteBuf, RpcResponse.class);
            nettyMessage.setBody(response);
        }

        if(log.isInfoEnabled()){
            log.info("nettyMessage = " + nettyMessage);
        }

      //  ReferenceCountUtil.release(byteBuf);
        return nettyMessage;
    }


}

package com.nz.rpc.netty.coder;


import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.serialization.AbstractSerialize;
import com.nz.rpc.serialization.SerializeFactory;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageBodyUtil {

    private static AbstractSerialize serialize =  SerializeFactory.getProtostuffSerialize();

    public static ByteBuf encoder(Object body, ByteBuf byteBuf){

        if(body == null){
            return  byteBuf;
        }

        try{
            byte[] bodyByte =  serialize.serialize(body);
          //  log.debug("bodyLength = {},readerIndex={},writerIndex={}" ,bodyByte.length,byteBuf.readerIndex(),byteBuf.writerIndex());
            byteBuf.writeInt(bodyByte.length);
            byteBuf.writeBytes(bodyByte);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }


        return byteBuf;
    }

    public static <T> T decoder(ByteBuf byteBuf,Class<T> clazz){

      //  log.debug("readerIndex={},writerIndex={}",byteBuf.readerIndex(),byteBuf.writerIndex());
        int bodyLength = byteBuf.readInt();

       // log.debug("body长度 = {},readerIndex={},writerIndex={}" ,bodyLength,byteBuf.readerIndex(),byteBuf.writerIndex());
        byte[] bodyByte = new byte[bodyLength];
        byteBuf.readBytes(bodyByte);
        T response = null;
        try{
            response = serialize.deserialize(bodyByte,clazz);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }

        return response;

    }

    public static void main(String args[])throws Exception{

        RpcRequest request = new RpcRequest();

        Object[] params = new Object[1];

        params[0] = (Object)123456L;

        request.setParameters(params);

        byte[] body =  serialize.serialize(request);

        RpcRequest request1 = serialize.deserialize(body,RpcRequest.class);

        log.info("request1 = " + request1);
    }




}

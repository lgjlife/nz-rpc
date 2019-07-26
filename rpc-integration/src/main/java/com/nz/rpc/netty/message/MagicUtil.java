package com.nz.rpc.netty.message;

import io.netty.buffer.ByteBuf;

public class MagicUtil {

    public static final long  MAGIC = 0xAABBA1B2C3D4E5F6l;

    public static ByteBuf writeMagicToBytebuf(ByteBuf byteBuf){
        byteBuf.writeLong(MAGIC);
        return byteBuf;
    }


    public static ByteBuf checkMagic(ByteBuf byteBuf){

        if(byteBuf.readableBytes() > 4){
            long magic =  byteBuf.readLong();
        }
        return null;

    }

}

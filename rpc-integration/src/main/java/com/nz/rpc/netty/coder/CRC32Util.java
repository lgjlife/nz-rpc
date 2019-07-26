package com.nz.rpc.netty.coder;

import java.util.zip.CRC32;

public class CRC32Util {


    public static int getValue(byte[] body) {
        CRC32 crc32 =   new CRC32();
        crc32.update(body);
        return (int)crc32.getValue()&0xFFFFFFFF;
    }

    public static void main(String args[]){

        System.out.println(CRC32Util.getValue("123456".getBytes()));
        System.out.println(CRC32Util.getValue("123456".getBytes()));
    }



}

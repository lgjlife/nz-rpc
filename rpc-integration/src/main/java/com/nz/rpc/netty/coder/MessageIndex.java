package com.nz.rpc.netty.coder;

public class MessageIndex {

   /* //一个字节
    private Byte type; //1
    //8个字节
    private long sessionID; //8
    private Integer length = 0; //4
    private int crcCode;*/

    public static int typeIndex = 0;
    public static int sessionIDIndex = 1;
    public static int lengthIndex = 9;
    public static int crcCodeIndex = 13;

    public static int bodyLengthIndex = 17;

    public static int bodyIndex = 21;



}

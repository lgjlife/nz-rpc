package com.nz.rpc.netty.message;


import java.util.Map;

/**
 *功能描述
 * @author lgj
 * @Description  netty自定义协议消息头
 * @date 4/16/19
*/
public class Header {

    //校验头
    private int crcCode;

    //消息头消息体的总长度
    private  int length;

    //全局唯一id
    private  long sessionId;

    //消息类型
    private  MessageType type;

    //扩展字段
    private Map<String,Object> attachment;
}

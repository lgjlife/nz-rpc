package com.nz.rpc.netty.message;

public enum MessageType {

    //业务请求消息
    APP_REQUEST_TYPE((byte)0),

    //业务响应消息
    APP_RESPONE_TYPE((byte)1),
    //one way 消息,只发送消息，无需回复
    ONE_WAY_TYPE((byte)2),
    //握手请求消息
    HANDSHAKE_REQUEST_TYPE((byte)3),
    //握手应答消息
    HANDSHAKE_RESPONSE_TYPE((byte)4),
    //心跳请求信息
    HEARTBEAT_REQUEST_TYPE((byte)5),
    //心跳应答消息
    HEARTBEAT_RESPONSE_TYPE((byte)6);


    private byte value;

    MessageType(byte value) {
        this.value = value;
    }
}

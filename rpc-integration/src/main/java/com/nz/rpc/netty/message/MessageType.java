package com.nz.rpc.netty.message;

public enum MessageType {

    //业务请求消息
    APP_REQUEST_TYPE((byte)10),

    //业务响应消息
    APP_RESPONE_TYPE((byte)11),
    //one way 消息,只发送消息，无需回复
    ONE_WAY_TYPE((byte)12),
    //握手请求消息
    HANDSHAKE_REQUEST_TYPE((byte)13),
    //握手应答消息
    HANDSHAKE_RESPONSE_TYPE((byte)14),
    //心跳请求信息
    HEARTBEAT_REQUEST_TYPE((byte)15),
    //心跳应答消息
    HEARTBEAT_RESPONSE_TYPE((byte)16);


    private byte value;

    MessageType(byte value) {
        this.value = value;
    }


    public byte getValue() {
        return value;
    }
}

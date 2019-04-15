package com.nz.rpc.netty.message;

import lombok.Data;


@Data
public class NettyMessage {

    //消息头
    private Header header;
    //消息体
    private Object body;
}

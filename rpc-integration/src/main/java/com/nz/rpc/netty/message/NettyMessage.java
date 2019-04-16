package com.nz.rpc.netty.message;

import lombok.Data;

import java.io.Serializable;


@Data
public class NettyMessage  implements Serializable {

    //消息头
    private Header header;
    //消息体
    private Object body;
}

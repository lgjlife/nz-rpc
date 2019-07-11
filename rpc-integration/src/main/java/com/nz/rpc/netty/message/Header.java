package com.nz.rpc.netty.message;


import lombok.Data;

import java.io.Serializable;

/**
 *功能描述
 * @author lgj
 * @Description  netty自定义协议消息头
 * @date 4/16/19
*/

@Data
public class Header implements Serializable {

    //消息类型
    private Byte type;
    //全局唯一id
    private long sessionID;
    //消息头消息体的总长度
    private Integer length = 0;
    //校验头
    private int crcCode;



    //扩展字段
    //private Map<String,Object> attachment;
}

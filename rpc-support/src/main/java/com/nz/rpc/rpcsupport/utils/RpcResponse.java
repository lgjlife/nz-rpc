package com.nz.rpc.rpcsupport.utils;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class RpcResponse implements Serializable {

    private String requestId;  //对应请求id
    private Exception exception; //失败抛出的异常
    private Object result;   //结果
}

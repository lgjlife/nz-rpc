package com.nz.rpc.msg;


import lombok.Data;

import java.io.Serializable;


/**
 *功能描述
 * @author lgj
 * @Description  RPC返回参数类
 * @date 4/12/19
*/
@Data
public class RpcResponse implements Serializable {

    private String responseId;  //对应请求id
    private Exception exception; //失败抛出的异常
    private Object result;   //结果
}

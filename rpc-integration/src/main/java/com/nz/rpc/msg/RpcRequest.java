package com.nz.rpc.msg;


import lombok.Data;

import java.io.Serializable;


/**
 *功能描述
 * @author lgj
 * @Description  Rpc请求参数类
 * @date 4/12/19
*/
@Data
public class RpcRequest implements Serializable {
    private String requestId;  //请求id
    private String className;  //调用类名
    private String methodName; //调用方法名
    private String[] parameterTypes; //方法参数类型
    private Object[] parameters;   //方法参数


}
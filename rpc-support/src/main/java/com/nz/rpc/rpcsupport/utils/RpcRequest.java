package com.nz.rpc.rpcsupport.utils;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class RpcRequest implements Serializable {
    private String requestId;  //请求id
    private String className;  //调用类名
    private String methodName; //调用方法名
    private Class<?>[] parameterTypes; //方法参数类型
    private Object[] parameters;   //方法参数
}

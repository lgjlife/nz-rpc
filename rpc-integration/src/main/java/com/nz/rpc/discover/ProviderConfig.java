package com.nz.rpc.discover;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderConfig implements Serializable{

    private static final long serialVersionUID = 1;
    //通信host
    private String host;
    //通信端口
    private Integer port;

    //请求接口名称
    private String interfaceName;
    private String implName;
    //请求方法
    private String[] methods;
    //应用名称
    private String application;
    //权重
    private int weight;
    //调用时间
    private int callTime;

}

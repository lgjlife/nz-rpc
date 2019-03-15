package com.nz.rpc.rpcsupport.utils;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class RegistryConfig implements Serializable {

    private static final long serialVersionUID = 1;
    //通信host
    private String host;
    //通信端口
    private int port;


    private String interfaceName;

    private String[] methods;

    private String application;


}

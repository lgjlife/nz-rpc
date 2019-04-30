package com.nz.rpc.netty.server.properties;

import lombok.Data;

@Data
public class NettyServerProperties {

    private int bossTheads = 2;
    private int workerTheads = 4;
}

package com.nz.rpc.rpcserver.config.zookeeper;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.host}")
    private String host;
    @Value("${zookeeper.port}")
    private String port;



    public String address() {
        return host + ":" + port;
    }

}

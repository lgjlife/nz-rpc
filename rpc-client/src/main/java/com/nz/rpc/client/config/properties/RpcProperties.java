package com.nz.rpc.client.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *功能描述 
 * @author lgj
 * @Description
 * @date 3/27/19
*/
@Data
@ConfigurationProperties("nzrpc.client")
public class RpcProperties {


    private String zhost;

    private int zport;

}

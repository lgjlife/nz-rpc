package com.nz.rpc.properties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *功能描述 
 * @author lgj
 * @Description
 * @date 3/27/19
*/
@Data
@ConfigurationProperties("nzrpc")
public class RpcProperties {


    @Value("${nzrpc.zookeeper.address}")
    private String zookeeperAdress;
    @Value("${nzrpc.data}")
    private String data;

}

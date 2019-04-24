package com.nz.rpc.properties;


import com.nz.rpc.uid.UidProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *功能描述 
 * @author lgj
 * @Description  Rpc参数配置类
 * @date 3/27/19
*/
@Data
@ConfigurationProperties(prefix="nzrpc")
public class RpcProperties {


    /*
     * zookeeper 连接地址： "localhost:2182,localhost:2183,localhost:2184"
     * 默认值： localhost:2182
    */
    @Value("${nzrpc.zookeeper.address:localhost:2182}")
    private String zookeeperAdress;

    /*
     * netty服务端口
     * 默认值：8321
     */
    @Value("${nzrpc.netty.port:8321}")
    private int nport;


    @Value("${nzrpc.netty.host:127.0.0.1}")
    private String nhost;

    /*
    * 被@RpcReference注解的消费者接口引用所在的类,如有多个中间使用","隔开
    *  nzrpc.scan-package: "com,org"
    */
    @Value("${nzrpc.scan-package:com}")
    private String scanPackage;


    /*
    动态代理方式　
    1. jdk　　　jdk动态代理
    2. cglib
    3. javassit

    默认值：jdk
     */
    @Value("${nzrpc.proxy-type:jdk}")
    private String proxyType;

    @Value("${nzrpc.serialization:hessian}")
    public  String serialization;

    private UidProperties uid;
}

package com.nz.rpc.serialization;

import com.nz.rpc.properties.RpcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *功能描述
 * @author lgj
 * @Description  序列化配置类
 * @date 4/16/19
*/
@Configuration
public class SerializationAutoConfiguration {

    @Autowired
    RpcProperties rpcProperties;

    @Bean
    public  AbstractSerialize serialize(){
        return SerializationCreate.create(rpcProperties.getSerialization());
    }
}

package com.app.consumer.config;


import com.app.common.service.DemoService;
import com.nz.rpc.context.ClientContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 *功能描述
 * @author lgj
 * @Description  异步调用方法配置
 * @date 5/6/19
*/
@Slf4j
@Configuration
public class AsyncMethodConfig {

    @PostConstruct
    public void asyncMethodConfig(){

        try{
            ClientContext.addAsyncMethod(DemoService.class.getMethod("setName",String.class,Long.class));
        }
        catch(Exception ex){
            log.error("AsyncMethodConfig Exception:"+ex);
        }

    }
}

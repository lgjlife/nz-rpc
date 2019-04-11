package com.nz.rpc.provider;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

@Slf4j
@Configuration
public class ProviderAutoConfiguration {

    @Autowired
    private  ApplicationContext context;


    @Bean
    public  ProviderDiscover providerDiscover(ProviderHandle providerHandle){

        ProviderDiscover providerDiscover = new ProviderDiscover(context);
        providerDiscover.discover(providerHandle);
        test(providerHandle);
        return  providerDiscover;
    }

    @Bean
    ProviderHandle providerHandle(){
        ProviderHandle providerHandle  =  new ProviderHandle();

        return  providerHandle;
    }

    void test(ProviderHandle handle){
        String interfaceName = "com.app.common.service.UserService";
        String clzName = handle.get(interfaceName);

        if (clzName!= null){

            try{
                Class clz = Class.forName(clzName);
               Object target = clz.newInstance();
                Method[] methods = clz.getDeclaredMethods();
                String methodName = clz.getName()+"."+"queryName";
                Method method = clz.getDeclaredMethod("queryName",String.class,Long.class);



                Object[] args = new Object[2];
                args[0] = "hhhh";
                args[1] = 12L;

                Object result = method.invoke(target,args);

                log.debug("result = {}",result);

            }
            catch(Exception ex){
                log.error("[{}]反射执行错误:{}",clzName,ex.getMessage());
            }


        }
    }
}

package com.nz.rpc.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderAutoConfiguration {

    @Autowired
    private  ApplicationContext context;


    @Bean
    public  ProviderDiscover providerDiscover(ProviderHandle providerHandle){

        ProviderDiscover providerDiscover = new ProviderDiscover(context);
        providerDiscover.discover(providerHandle);
        return  providerDiscover;
    }

    @Bean
    ProviderHandle providerHandle(){
        ProviderHandle providerHandle  =  new ProviderHandle();

        return  providerHandle;
    }
}

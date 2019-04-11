package com.nz.rpc.provider;

import com.nz.rpc.anno.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;


@Slf4j
public class ProviderDiscover   {

    private  ApplicationContext context;



    public ProviderDiscover(ApplicationContext context) {
        this.context = context;
    }




    /**
     *功能描述
     * @author lgj
     * @Description   获取@RpcService注解的类
     * @date 4/11/19
     * @param:
     * @return:
     *
    */
    public void discover(ProviderHandle providerHandle) {

        Map<String, Object> providers = context.getBeansWithAnnotation(RpcService.class);
        if(providers != null){


            providers.forEach((k,v)->{
                Class[] interfaces = v.getClass().getInterfaces();
                log.debug("{}:providers clz = {},Interfaces = {}",k,v.getClass().getName(),interfaces);

                //存在多个接口的情况
                for(Class clz:interfaces){
                    providerHandle.put(clz.getName(),v.getClass().getName());
                }

            });

        }
    }
}

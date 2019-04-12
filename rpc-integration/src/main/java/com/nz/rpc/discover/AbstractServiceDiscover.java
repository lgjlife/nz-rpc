package com.nz.rpc.discover;

import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.anno.RpcService;
import com.nz.rpc.provider.ProviderHandle;
import com.nz.rpc.proxy.RpcProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *功能描述
 * @author lgj
 * @Description 服务注册与发现抽象类
 * @date 4/12/19
*/

@Slf4j
public  abstract  class AbstractServiceDiscover {

    private  ApplicationContext  context;

    ProviderHandle providerHandle;
    /**
     *功能描述
     * @author lgj
     * @Description  查找被@RpcService注解的类
     * @date 4/12/19
     * @param:
     * @return:
     *
    */
    public  void  providerDiscover(){

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


    /**
     *功能描述
     * @author lgj
     * @Description   查找被@RpcReference注解的消费者接口引用
     * @date 4/12/19
     * @param:
     * @return:
     *
    */
    public  void consumerDiscover(String scanPackage){

        String[] scanPackages = scanPackage.split(".");

        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        Set<Field> serviceNames = new HashSet<>();

        for(String v:scanPackages){
            Reflections reflections = new Reflections(v,new FieldAnnotationsScanner());
            Set<Field> fields = reflections.getFieldsAnnotatedWith(RpcReference.class);
            serviceNames.addAll(fields);
        }


        serviceNames.forEach((v)->{
            log.debug("Field: {},class={}",v.getName(),v.getType().getName());
        });

        for(Field serviceName:serviceNames){
            String className = serviceName.getType().getName();
            log.debug("RpcReference 注解类："+className);

            try{
                Class clazz = Class.forName(className);

                listableBeanFactory.registerBeanDefinition(clazz.getName(),
                        BeanDefinitionBuilder
                                .genericBeanDefinition(new RpcProxyFactory().createInstance(clazz,false).getClass())
                                .getBeanDefinition());

            }
            catch(Exception ex){
                log.error("创建代理对象失败[{className}]:{}",className,ex.getMessage());
            }

        }
    }


    public  abstract  void registerService();

    public  abstract  void queryService();




}

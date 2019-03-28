package com.nz.rpc.client.proxy;

import com.nz.rpc.client.config.properties.RpcProperties;
import com.nz.rpc.rpcsupport.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;


@Slf4j
public class RpcProxyRegister implements ApplicationContextAware, InitializingBean,
        BeanDefinitionRegistryPostProcessor
{

    private ApplicationContext context;

    private DefaultListableBeanFactory listableBeanFactory;

    private RpcProperties properties;


    private RpcProxyFactory proxyFactory;

    public void setProxyFactory(RpcProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public void setProperties(RpcProperties properties) {
        this.properties = properties;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        log.debug("+++++++++++postProcessBeanDefinitionRegistry");

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Demo.class);
        BeanDefinition definition = builder.getBeanDefinition();

        listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        listableBeanFactory.registerBeanDefinition("demo", definition);
        properties = new RpcProperties();
        properties.setScan("com");
        String[] scanPackages = properties.getScan().split(",");


        Set<Field> serviceNames = new HashSet<>();

        for(String s:scanPackages){

            log.debug("scanPackages = " + s);
            Reflections reflections1 = new Reflections(s,new FieldAnnotationsScanner());
            Set<Field> fields = reflections1.getFieldsAnnotatedWith(RpcReference.class);
            serviceNames.addAll(fields);
        }


        for(Field serviceName:serviceNames){
            String className = serviceName.getType().getName();
            log.debug("RpcReference 注解类："+className);

            try{
                Class clazz = Class.forName(className);
                if(proxyFactory == null){
                    log.debug("proxyFactory is null");
                }
                else{
                    log.debug("proxyFactory info = " + proxyFactory.getClass());
                }

                listableBeanFactory.registerBeanDefinition(clazz.getName(),
                        BeanDefinitionBuilder
                                .genericBeanDefinition(new RpcProxyFactory().createInstance(clazz,false).getClass())
                                .getBeanDefinition());

            }
            catch(Exception ex){
                ex.printStackTrace();
            }

        }


    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /*listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        listableBeanFactory.registerSingleton( Demo.class.getName(),new Demo());
        Demo demo = context.getBean(Demo.class);
        System.out.println(demo.func());

        Reflections reflections1 = new Reflections("com.*",new FieldAnnotationsScanner());
        Set<Field> serviceNames = reflections1.getFieldsAnnotatedWith(RpcReference.class);

        for(Field serviceName:serviceNames){
            String className = serviceName.getType().getName();
            log.debug("RpcReference 注解类："+className);

            try{
                Class clazz = Class.forName(className);
                if(proxyFactory == null){
                    log.debug("proxyFactory is null");
                }
                else{
                    log.debug("proxyFactory info = " + proxyFactory.getClass());
                }

                listableBeanFactory.registerSingleton(clazz.getName(),proxyFactory.createInstance(clazz,false));

              *//*  listableBeanFactory.registerBeanDefinition("RpcDemoService",
                        BeanDefinitionBuilder.genericBeanDefinition(Class.forName(proxyFactory.createInstance(clazz,false).getClass().getName()))
                                .getBeanDefinition());*//*

                RpcDemoService bean = context.getBean(RpcDemoService.class);
                bean.func1("adsdd");
                if(bean == null){
                    log.debug("bean is null");
                }
                else{
                    log.debug("bean info = " + bean.getClass());
                }

            }
            catch(Exception ex){
                ex.printStackTrace();
            }

        }*/



    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.debug("RpcProxyRegister setApplicationContext......");
        this.context = context;
       // listableBeanFactory =  (DefaultListableBeanFactory)context.getAutowireCapableBeanFactory();


    }

}

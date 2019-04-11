package com.nz.rpc.proxy;

import com.nz.rpc.anno.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.BeansException;
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


/**
 *功能描述
 * @author lgj
 * @Description  将@RpcReference注解的接口创建代理对象，并将代理对象注入bean容器中
 *                1. ApplicationContextAware 用于获取 ApplicationContext
 *                2. BeanDefinitionRegistryPostProcessor可以在创建其他@Service注解的bean之前执行。
 *                   先注入接口，以免@RpcReference出现注入失败的情况
 * @date 4/11/19
*/
@Slf4j
public class RpcProxyRegister implements BeanDefinitionRegistryPostProcessor , ApplicationContextAware {


    private RpcProxyFactory proxyFactory;
    private ApplicationContext context;

    public void setProxyFactory(RpcProxyFactory proxyFactory) {

        this.proxyFactory = proxyFactory;
    }

    /**
     *功能描述
     * @author lgj
     * @Description   查找@RpcReference注解的接口，并创建代理对象，注入容器
     * @date 4/11/19
     * @param:
     * @return:
     *
    */
    public  void discoverProxy(){

        DefaultListableBeanFactory  listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        Set<Field> serviceNames = new HashSet<>();

        Reflections reflections = new Reflections("com",new FieldAnnotationsScanner());
        Set<Field> fields = reflections.getFieldsAnnotatedWith(RpcReference.class);
        serviceNames.addAll(fields);

        serviceNames.forEach((v)->{
            log.debug("Field: {},class={}",v.getName(),v.getType().getName());
        });

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
                log.error("创建代理对象失败[{className}]:{}",className,ex.getMessage());
            }

        }


    }

    /**
     *功能描述
     * @author lgj
     * @Description  容器启动时将会执行
     * @date 4/11/19
     * @param:
     * @return:
     *
    */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        discoverProxy();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    /**
     *功能描述
     * @author lgj
     * @Description  获取  context
     * @date 4/11/19
     * @param:
     * @return:
     *
    */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.debug("RpcProxyRegister setApplicationContext......");
        this.context = context;
    }

}

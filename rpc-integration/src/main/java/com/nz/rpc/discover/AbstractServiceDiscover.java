package com.nz.rpc.discover;

import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.anno.RpcService;
import com.nz.rpc.common.TestDemo;
import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.provider.ProviderHandle;
import com.nz.rpc.proxy.RpcProxyFactory;
import com.nz.rpc.zk.ZkCli;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
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
public  abstract  class AbstractServiceDiscover
       // implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware
{

    protected  ZkCli zkCli;

    protected   ApplicationContext  context;

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    protected RpcProperties properties;
    protected  ProviderHandle providerHandle;

    public void setProviderHandle(ProviderHandle providerHandle) {
        this.providerHandle = providerHandle;
    }

    public void setZkCli(ZkCli zkCli) {
        this.zkCli = zkCli;
    }

    public void setProperties(RpcProperties properties) {
        this.properties = properties;
    }

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
    public  void consumerDiscover(){

        String[] scanPackages = properties.getScanPackage().split("\\.");

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

                /*listableBeanFactory.registerBeanDefinition(clazz.getName(),
                        BeanDefinitionBuilder
                                .genericBeanDefinition(new RpcProxyFactory().createInstance(clazz).getClass())

                                .getBeanDefinition());*/
                listableBeanFactory.registerSingleton(classToBeanName(className),new RpcProxyFactory().createInstance(clazz));


                String beanName = clazz.getName();
                Object bean =  listableBeanFactory.getBean(classToBeanName(className));
             //   Object bean = listableBeanFactory.getBeanDefinition(beanName);


                Class controllerClazz = serviceName.getDeclaringClass();

                Object controllerBean = listableBeanFactory.getBean(classToBeanName(controllerClazz.getName()));

                Field[] fields = controllerBean.getClass().getDeclaredFields();

                for(Field field : fields){
                    if(field.getType().getName().equals(className)){
                        log.debug("匹配成功！");
                        field.setAccessible(true);
                        field.set(controllerBean,bean);

                    }
                }


                log.debug("controller = " + controllerClazz.getName() + controllerBean);

                log.debug("创建代理对象成功[{}],bena={}",className,bean.getClass().getName());
            }
            catch(Exception ex){
                log.error("创建代理对象失败[{}]:{}",className,ex);
            }

        }
    }

    public void test(){
        TestDemo demo = new TestDemo();
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        listableBeanFactory.registerSingleton("demo",demo);

        TestDemo demo1 = (TestDemo) listableBeanFactory.getBean("demo");

        log.debug("ssssss");
    }

    public  abstract  void registerService();

    public  abstract  void queryService();



    //
    public static  String classToBeanName(String className){

        int dot = className.lastIndexOf(".");

        String beanName = className.substring(dot + 1,className.length());

        byte[] bts = beanName.getBytes();

        if( (bts[0] >= 65 ) && (bts[0] <= 90)){
            bts[0] +=   32;
        }
        beanName = new String(bts);

        return  beanName;
    }

    public static void main(String args[]){

        String result = classToBeanName("com.app.common.service.ZserService");
        System.out.println(result);
    }
/*
    *//**
     *功能描述
     * @author lgj
     * @Description  容器启动时将会执行
     * @date 4/11/19
     * @param:
     * @return:
     *
     *//*
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        log.debug("RpcProxyRegister postProcessBeanDefinitionRegistry......");
        this.consumerDiscover();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    *//**
     *功能描述
     * @author lgj
     * @Description  获取  context
     * @date 4/11/19
     * @param:
     * @return:
     *
     *//*
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.debug("RpcProxyRegister setApplicationContext......");
        this.context = context;

        this.consumerDiscover();
    }*/


}

package com.nz.rpc.discover;

import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.anno.RpcService;
import com.nz.rpc.netty.NettyContext;
import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.proxy.RpcProxyFactory;
import com.nz.rpc.zk.ZkCli;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 *功能描述
 * @author lgj
 * @Description 服务注册与发现抽象类
 * @date 4/12/19
*/

@Data
@Slf4j
public  abstract  class AbstractServiceDiscover{

    protected  ZkCli zkCli;

    protected   ApplicationContext  context;

    protected RpcProperties properties;


    private RpcProxyFactory rpcProxyFactory;

    public AbstractServiceDiscover() {
    }

    public AbstractServiceDiscover(ZkCli zkCli) {
        this.zkCli = zkCli;
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
            providers.forEach((beanName,bean)->{

                Class[] interfaces = bean.getClass().getInterfaces();
                log.debug("{}:providers clz = {},Interfaces = {}",beanName,bean.getClass().getName(),interfaces);

                //存在多个接口实现类，使用linst存储的情况
                for(Class inter:interfaces){
                    List<String> serviceImplLists = NettyContext.getLocalServiceImplMap().get(inter.getName());
                    if(serviceImplLists == null){
                        serviceImplLists = new ArrayList<>();
                    }
                    serviceImplLists.add(bean.getClass().getName());
                    NettyContext.getLocalServiceImplMap().put(inter.getName(),serviceImplLists);
                }

            });

        }
    }


    /**
     *功能描述
     * @author lgj
     * @Description   １.查找被@RpcReference注解的消费者接口引用
     * 　　　　　　　　 2. 创建动态代理
     * 　　　　　　　　　3　注入bean容器
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



        for(Field serviceName:serviceNames){
            String className = serviceName.getType().getName();
            log.debug("@RpcReference class："+className);

            try{
                Class clazz = Class.forName(className);

                listableBeanFactory.registerSingleton(classToBeanName(className),
                        rpcProxyFactory.createInstance(clazz));

                Object bean =  listableBeanFactory.getBean(classToBeanName(className));

                Class controllerClazz = serviceName.getDeclaringClass();


                Object controllerBean = listableBeanFactory.getBean(classToBeanName(controllerClazz.getName()));

                Field[] fields = controllerBean.getClass().getDeclaredFields();

                for(Field field : fields){
                    if(field.getType().getName().equals(className)){
                        log.debug("set bean to the controler field");
                        field.setAccessible(true);
                        field.set(controllerBean,bean);
                    }
                }
                log.debug("create proxy object [{}] success ,bena={}",className,bean.getClass().getName());
            }
            catch(Exception ex){
                log.error("create proxy object[{}] fail:{}",className,ex);
            }

        }
    }



    public  abstract  void registerService();

    public  abstract  void queryService();

    /**
     *功能描述
     * @author lgj
     * @Description   class　name 转化　为　bean name
     *                com.nz.rpc.UserDemo --> userDemo
     * @date 4/14/19
     * @param:
     * @return:
     *
    */
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



}

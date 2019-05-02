package com.nz.rpc.proxy;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class JavassistProxyFactory02 {

    /*
     * 要代理的对象的class
     * */
    @SuppressWarnings("deprecation")
    public Object getProxy(Class clazz) throws InstantiationException, IllegalAccessException {
        // 代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        // 设置需要创建子类的父类
        proxyFactory.setSuperclass(clazz);
        /*
         * 定义一个拦截器。在调用目标方法时，Javassist会回调MethodHandler接口方法拦截，
         * 来实现你自己的代理逻辑，
         * 类似于JDK中的InvocationHandler接口。
         */

        proxyFactory.setHandler(new MethodHandler() {
            /*
             * self为由Javassist动态生成的代理类实例，
             *  thismethod为 当前要调用的方法
             *  proceed 为生成的代理类对方法的代理引用。
             *  Object[]为参数值列表，
             * 返回：从代理实例的方法调用返回的值。
             *
             * 其中，proceed.invoke(self, args);
             *
             * 调用代理类实例上的代理方法的父类方法（即实体类ConcreteClassNoInterface中对应的方法）
             */
           /* public Object invoke(Object self, Method thismethod, Method proceed, Object[] args) throws Throwable {
                System.out.println("--------------------------------");
                System.out.println(self.getClass());
                //class com.javassist.demo.A_$$_javassist_0
                System.out.println("代理类对方法的代理引用:"+thismethod.getName());
                System.out.println("开启事务 -------");

                Object result = proceed.invoke(self, args);

                System.out.println("提交事务 -------");
                return result;
            }*/

            @Override
            public Object invoke(Object o, Method method, Method method1, Object[] objects) throws Throwable {
                return null;
            }
        });




        // 通过字节码技术动态创建子类实例
        return  proxyFactory.createClass().newInstance();
    }

    // 代理工厂创建动态代理
    public  static  void testJavassistFactoryProxy() throws Exception {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();

        // 设置被代理类的类型
       // proxyFactory.setSuperclass(Demo3.class);
        Class[] cla= new Class[1];
        cla[0] = Demo3.class;
        proxyFactory.setInterfaces(cla);
        // 创建代理类的class
        Class proxyClass = proxyFactory.createClass();

        // 创建对象
        Demo3 proxyTest = (Demo3)proxyClass.newInstance();

        ((ProxyObject) proxyTest).setHandler(new MethodHandler() {
            // 真实主题
          /*  Demo2 test = new Demo2();

            public Object invoke(Object self, Method thisMethod,
                                 Method proceed, Object[] args) throws Throwable {
                String before = "before ";
                Object str = thisMethod.invoke(test, args);
                String after = " after";
                return before + str + after;
            }*/


            @Override
            public Object invoke(Object o, Method method, Method method1, Object[] objects) throws Throwable {

                System.out.println("javassist");
                Object str = method1.invoke(o, objects);
                return str;
            }
        });
        proxyTest.func();

    }



    public static void main(String args[]){

       try{
           testJavassistFactoryProxy();
       }
       catch(Exception ex){
          ex.printStackTrace();
       }
       /* JavassistProxyFactory02 create = new JavassistProxyFactory02();
        try{
            Demo2 de = (Demo2)create.getProxy(Demo2.class);
            de.func();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }*/



    }





}

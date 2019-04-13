package com.nz.rpc.proxy;

import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.utils.uid.UUidProducer;
import com.nz.rpc.utils.uid.UidProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 功能描述
 *
 * @author lgj
 * @Description 动态代理
 * @date 1/27/19
 */

@Slf4j
public class RpcInvoker implements InvocationHandler, MethodInterceptor {

    private  UidProducer uidProducer = new UUidProducer();
    /**
     * 功能描述
     *
     * @author lgj
     * @Description jdk
     * @date 1/27/19
     * @param:
     * @return:
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        return doIncoke(method, args);
    }


    /**
     * 功能描述
     *
     * @author lgj
     * @Description cglib
     * @date 1/27/19
     * @param:
     * @return:
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return doIncoke(method, args);
    }

    public Object doIncoke(Method method, Object[] args) {
        Object result = null;

        log.debug("++++++++++++++++++++++++++++++++RpcInvoker  doIncoke .....");

        RpcRequest request = buildRequest(method,args);
        log.debug("RPC请求数据:{}",request);

        //todo
        //1.获取消费提供者信息
        //2.负载均衡选择服务提供者
        //3.创建连接获取channle
        //4.发送请求
        //5.注册回调函数
        //6.超时处理


        return "执行动态代理成功";
    }

    private RpcRequest  buildRequest(Method method, Object[] args){
        String[]   classes = new String[args.length];

        for(int i = 0; i< args.length ; i++){
            classes[i] = args[i].getClass().getName();

        }
        RpcRequest  request = new RpcRequest();
        request.setRequestId(uidProducer.getUid());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(classes);
        request.setParameters(args);
        return  request;
    }
}

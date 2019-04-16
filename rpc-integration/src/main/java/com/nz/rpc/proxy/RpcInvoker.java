package com.nz.rpc.proxy;

import com.nz.rpc.msg.MsgRequestHandler;
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


        MsgRequestHandler requestHandler =  MsgRequestHandler.getInstance();
        long uid = requestHandler.doRequest(request);

        return requestHandler.result(uid);
    }

    private RpcRequest  buildRequest(Method method, Object[] args){
        String[]   classes = new String[args.length];

        for(int i = 0; i< args.length ; i++){
            classes[i] = args[i].getClass().getName();

        }
        RpcRequest  request = new RpcRequest();
        request.setRequestId(uidProducer.getUidForLong());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(classes);
        request.setParameters(args);
        return  request;
    }
}

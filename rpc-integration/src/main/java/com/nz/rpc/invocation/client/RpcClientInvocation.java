package com.nz.rpc.invocation.client;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.uid.CustomProducer;
import com.nz.rpc.uid.UidProducer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;


@Slf4j
public class RpcClientInvocation implements ClientInvocation {

    private Method method;
    private Object[] args;

    private RpcRequest rpcRequest;

    private UidProducer uidProducer = new CustomProducer(0);

    private List<Interceptor> interceptors;

    private int index = 0;

    public RpcClientInvocation(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        rpcRequest=buildRequest(method,args);
    }

    public RpcClientInvocation(Method method, Object[] args, List<Interceptor> interceptors) {
        this.method = method;
        this.args = args;
        this.interceptors = interceptors;
    }

    @Override
    public Object executeNext() throws Exception {


        if(index == interceptors.size()-1){

            System.out.println("执行结束...");
            return "finish";
        }
        return  interceptors.get(index++).intercept(this);
    }


    private RpcRequest buildRequest(Method method, Object[] args){
        String[]   classes = new String[args.length];

        for(int i = 0; i< args.length ; i++){
            classes[i] = args[i].getClass().getName();

        }
        RpcRequest  request = new RpcRequest();
        request.setRequestId(uidProducer.getUid());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(classes);
        request.setParameters(args);
        return  request;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }
}

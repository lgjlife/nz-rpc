package com.nz.rpc.msg.request;

import com.nz.rpc.msg.RpcRequest;

import java.lang.reflect.Method;

public abstract class AbstractRequestHandler implements RequestHandler {


    @Override
    public Object invoke(Method method, Object[] args) {
        return null;
    }



    public abstract Object doInvoke();

    private RpcRequest buildRequest(Method method, Object[] args){
        String[]   classes = new String[args.length];

        for(int i = 0; i< args.length ; i++){
            classes[i] = args[i].getClass().getName();

        }
        RpcRequest  request = new RpcRequest();
       // request.setRequestId(uidProducer.getUid());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(classes);
        request.setParameters(args);
        return  request;
    }


}

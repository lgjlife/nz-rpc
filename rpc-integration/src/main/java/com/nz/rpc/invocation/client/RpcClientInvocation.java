package com.nz.rpc.invocation.client;

import com.nz.rpc.context.ClientContext;
import com.nz.rpc.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class RpcClientInvocation implements ClientInvocation {

    private Method method;
    private Object[] args;

    private List<Interceptor> interceptors;

    private int index = 0;

    private Map<String, String> attachments = new ConcurrentHashMap<>();

    public RpcClientInvocation(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        this.interceptors = ClientContext.interceptors;
    }

    public RpcClientInvocation(Method method, Object[] args, List<Interceptor> interceptors) {
        this.method = method;
        this.args = args;

    }

    @Override
    public Object executeNext() throws RuntimeException {
        Object result = null;
        if (index < interceptors.size()){
            result =  interceptors.get(index++).intercept(this);
        }
        return result;
    }


    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Map<String, String> getAttachments() {
        return attachments;
    }
}

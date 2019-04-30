package com.nz.rpc.interceptor.impl;

import com.nz.rpc.constans.RpcClientConstans;
import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.msg.ClientMessageHandler;
import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import com.nz.rpc.uid.UidProducer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;


@Slf4j
public class RpcClientRequestInterceptor implements Interceptor {

    private UidProducer uidProducer ;

    private ClientMessageHandler handler;

    public RpcClientRequestInterceptor(UidProducer uidProducer, ClientMessageHandler handler) {
        this.uidProducer = uidProducer;
        this.handler = handler;
    }

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        Object result = null;
        //log.info("正在执行RpcRequestInterceptor....");
        //准备消息
        RpcRequest request = buildRequest(invocation.getMethod(),invocation.getArgs());

        NettyMessage nettyMessage = buildNettyMessage(request);

        result = handler.sendRequest(invocation.getAttachments().get(RpcClientConstans.NETTY_REQUEST_HOST),
                invocation.getAttachments().get(RpcClientConstans.NETTY_REQUEST_PORT),
                nettyMessage);
        log.info("request result =[{}]",result);
        return result;
    }

    private NettyMessage buildNettyMessage(RpcRequest request){
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.APP_REQUEST_TYPE);
        nettyMessage.setHeader(header);
        nettyMessage.setBody(request);
        return  nettyMessage;
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


}

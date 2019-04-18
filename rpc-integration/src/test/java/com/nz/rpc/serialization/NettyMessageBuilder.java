package com.nz.rpc.serialization;

import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;

import java.util.LinkedHashMap;
import java.util.Map;

public class NettyMessageBuilder {

    public  static NettyMessage build(){

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        RpcRequest request = new RpcRequest();

        header.setCrcCode(1234);
        header.setType(MessageType.APP_RESPONE_TYPE);
        header.setLength(100);
        header.setSessionId(200);

        Map<String,Object> map = new LinkedHashMap<>();

        map.put("demoKey",(Object)"demoValue");
        header.setAttachment(map);


        request.setInterfaceName("com.demo");
        String[] types = {"java.lang.String" ,"java.lang.Integer"};
        String[] param = {"java.lang.String" ,"java.lang.Integer"};
        request.setParameterTypes(types);
        request.setParameters(param);
        request.setMethodName("buy");
        request.setRequestId(123456);


        message.setHeader(header);
        message.setBody(request);

        return  message;
    }

}

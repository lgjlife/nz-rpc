package com.nz.rpc.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;
import com.nz.rpc.serialization.anno.SerializationException;

import java.util.LinkedHashMap;
import java.util.Map;

public class JacksonSerializeUtil   {

    private ObjectMapper objectMapper = new ObjectMapper();


   // @Override
    public <T> byte[] serialize(T obj) throws SerializationException {
        if(obj == null){
            return new byte[0];
        }

        try{
            return   this.objectMapper.writeValueAsBytes(obj);
        }
        catch(Exception ex){
            throw new SerializationException("Jackson Serialize Fail!"+ex.getMessage(),ex.getCause());
        }
    }

 //   @Override
    public Object deserialize(byte[] data) throws SerializationException {

        return   deserialize(data,Object.class);
    }


  //  @Override
    public Object deserialize(byte[] data, Class clazz) throws SerializationException {

        try{
            return    this.objectMapper.readValue(data,clazz);
        }
        catch(Exception ex){
            throw new SerializationException("Jackson Serialize Fail!"+ex.getMessage(),ex.getCause());
        }

    }

    public static void main(String args[]){

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


        JacksonSerializeUtil serializeUtil = new JacksonSerializeUtil();

        try{
           byte[] data =  serializeUtil.serialize(message);
            System.out.println("序列化:"+ new String(data));
            NettyMessage nettyMessage = (NettyMessage)serializeUtil.deserialize(data);

            System.out.println("反序列化:"+ nettyMessage);

        }
        catch(Exception ex){
            System.out.println(ex);
        }



    }
}

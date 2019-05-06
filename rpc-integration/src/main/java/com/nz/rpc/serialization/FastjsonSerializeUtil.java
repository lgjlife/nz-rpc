package com.nz.rpc.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nz.rpc.msg.RpcRequest;
import com.nz.rpc.netty.message.Header;
import com.nz.rpc.netty.message.MessageType;
import com.nz.rpc.netty.message.NettyMessage;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 *功能描述
 * @author lgj
 * @Description fastjson ,对象属性需要实现get/set
 * @date 3/24/19
*/
public class FastjsonSerializeUtil  extends AbstractSerialize {

    public <T> byte[] serialize(T obj) {
        if (obj  == null){
            throw new NullPointerException();
        }

        String json = JSON.toJSONString(obj);
        byte[] data = json.getBytes();
        return data;
    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {

        T obj = JSON.parseObject(new String(data),clazz);
        return obj;
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


        AbstractSerialize serializeUtil = new FastjsonSerializeUtil();
        byte[] data = serializeUtil.serialize(message);
        NettyMessage nettyMessage = serializeUtil.deserialize(data,NettyMessage.class);
        Object object = nettyMessage.getBody();
        System.out.println(JSONObject.class.isAssignableFrom(object.getClass()));
        System.out.println(object.getClass().getName());

        JSONObject jsonObject = (JSONObject)object;
        System.out.println(jsonObject);
        RpcRequest request1 = JSONObject.parseObject(jsonObject.toString(),RpcRequest.class);

        System.out.println(request1);
     /*   try{
            String json = JSON.toJSONString(message);
            NettyMessage nettyMessage = (NettyMessage)JSON.parseObject(json,Object.class);
            System.out.println(nettyMessage);

        }
        catch(Exception ex){
            System.out.println(ex);
        }*/



    }
}

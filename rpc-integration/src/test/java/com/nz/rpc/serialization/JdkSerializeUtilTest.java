package com.nz.rpc.serialization;

import com.nz.rpc.netty.message.NettyMessage;
import org.junit.Test;



public class JdkSerializeUtilTest {

    @Test
    public void testJdkSerialize() throws Exception{

        AbstractSerialize serialize = new JdkSerializeUtil();

        NettyMessage message =  NettyMessageBuilder.build();

        NettyMessage result = null;
        for(int i = 0; i< 10; i++){
            long start = System.nanoTime();
            byte[] serByte = serialize.serialize(message);
            long end = System.nanoTime();
            System.out.println("jdk序列化时间："+ (end-start) + "ns");


            start = System.nanoTime();
            result  = serialize.deserialize(serByte,NettyMessage.class);
            end = System.nanoTime();
            System.out.println("jdk反序列化时间："+ (end-start) + "ns");
        }


        System.out.println("结果:" + result);

    }
}
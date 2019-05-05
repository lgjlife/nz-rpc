package com.nz.rpc.serialization;

import com.nz.rpc.netty.message.NettyMessage;
import org.junit.Test;


public class FastJsonSerializeUtilTest {

    @Test
    public void testFastJsonSerialize(){

        AbstractSerialize serialize = new ProtostuffSerializeUtil();

        NettyMessage message =  NettyMessageBuilder.build();

        TimeUtil timeUtil = new TimeUtil();
        TimeUtil timeUtil1 = new TimeUtil();

        NettyMessage result = null;
        byte[]  serByte = serialize.serialize(message);
        System.out.println("字节长度:" + serByte.length);
        result  = serialize.deserialize(serByte,NettyMessage.class);
        for(int i = 0; i< 100000; i++){
            //timeUtil.init();
            timeUtil.start();
            serByte = serialize.serialize(message);
            timeUtil.end();
            //System.out.println("序列化时间："+ timeUtil.getAvrTimeUs() + " Us");

            timeUtil1.start();
            result  = serialize.deserialize(serByte,NettyMessage.class);
            timeUtil1.end();
        }
        System.out.println("序列化时间："+ timeUtil.getAvrTimeUs() + " Us");
        System.out.println("反序列化时间："+ timeUtil1.getAvrTimeUs() + " Us");

        System.out.println("结果:" + result);

    }

    /**
     *功能描述
     * @author lgj
     * @Description  序列化框架并发测试
     * @date 4/18/19
     * @param:
     * @return:
     *
    */
    //@Test
    public void testThread(){

        for(int i = 0; i< 1000000; i++){

           new MyThread().start();

        }


    }


}

class MyThread extends  Thread{

    AbstractSerialize serialize = new FastjsonSerializeUtil()  ;

    NettyMessage message =  NettyMessageBuilder.build();

    @Override
    public void run() {

        NettyMessage result = null;
        byte[]  serByte = serialize.serialize(message);
        result  = serialize.deserialize(serByte,NettyMessage.class);

    }
}
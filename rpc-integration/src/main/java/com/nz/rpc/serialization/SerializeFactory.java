package com.nz.rpc.serialization;

/**
 *功能描述
 * @author lgj
 * @Description 序列化工厂类
 * @date 7/11/19
*/
public class SerializeFactory {

    private static volatile AbstractSerialize fastjsonSerialize;
    private static volatile AbstractSerialize hessianSerialize;
    private static volatile AbstractSerialize jacksonSerialize;
    private static volatile AbstractSerialize jdkjsonSerialize;
    private static volatile AbstractSerialize protostuffSerialize;


    public static AbstractSerialize getFastjsonSerialize() {

        if(fastjsonSerialize == null){
            synchronized (FastjsonSerializeUtil.class){
                if(fastjsonSerialize == null){
                    fastjsonSerialize = new FastjsonSerializeUtil();
                }
            }
        }

        return fastjsonSerialize;
    }

    public static AbstractSerialize getHessianSerialize() {

        if(hessianSerialize == null){
            synchronized (HessianSerializeUtil.class){
                if(hessianSerialize == null){
                    hessianSerialize = new HessianSerializeUtil();
                }
            }
        }

        return hessianSerialize;
    }

    public static AbstractSerialize getJacksonSerialize() {

        if(jacksonSerialize == null){
            synchronized (JacksonSerializeUtil.class){
                if(jacksonSerialize == null){
                    jacksonSerialize = new JacksonSerializeUtil();
                }
            }
        }

        return jacksonSerialize;
    }

    public static AbstractSerialize getJdkjsonSerialize() {

        if(jdkjsonSerialize == null){
            synchronized (JdkSerializeUtil.class){
                if(jdkjsonSerialize == null){
                    jdkjsonSerialize = new JdkSerializeUtil();
                }
            }
        }
        return jdkjsonSerialize;
    }

    public static AbstractSerialize getProtostuffSerialize() {

        if(protostuffSerialize == null){
            synchronized (ProtostuffSerializeUtil.class){
                if(protostuffSerialize == null){
                    protostuffSerialize = new ProtostuffSerializeUtil();
                }
            }
        }
        return protostuffSerialize;
    }
}

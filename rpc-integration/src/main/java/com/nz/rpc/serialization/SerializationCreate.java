package com.nz.rpc.serialization;

public class SerializationCreate {

    public  static  AbstractSerialize create(String type){

        switch (type){
            case "fastjson": return  new FastjsonSerializeUtil();
            case "jdk": return  new JdkSerializeUtil();
            case "hessian": return  new HessianSerializeUtil();
            default: return  new JdkSerializeUtil() ;
        }
    }
}

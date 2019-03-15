package com.nz.rpc.rpcsupport.serialize;

public interface Serialize {

    Object deserialize(byte[] b);

    Object deserialize(byte[] b, Class clazz);

    byte[] serialize(Object obj);
}

package com.nz.rpc.serialization;

public abstract class AbstractSerialize {

    public  abstract   <T> byte[] serialize(T obj) throws Exception;
    public abstract  <T> T deserialize(byte[] data, Class<T> clazz)  throws Exception;
}

package com.nz.rpc.serialization;


import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;


@Slf4j
public class HessianSerializeUtil extends AbstractSerialize {

    public <T> byte[] serialize(T obj) {

        if (obj  == null){
            throw new NullPointerException();
        }
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            HessianOutput ho = new HessianOutput(bos);
            ho.writeObject(obj);

            return  bos.toByteArray();
        }
        catch(Exception ex){
            log.error("HessianSerializeUtil序列化发生异常!"+ex);
            throw new  RuntimeException();
        }

    }

    public <T> T deserialize(byte[] data, Class<T> clazz) {

        if (data == null){
            throw  new  NullPointerException();
        }
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            HessianInput hi = new HessianInput(bis);
            return (T)hi.readObject();

        }
        catch(Exception ex){
            log.error("HessianSerializeUtil反序列化发生异常!"+ex);
            throw new  RuntimeException();
        }

    }

    public static void main(String args[]){

        AsyncFuture future = new HessianSerializeUtil().new AsyncFuture();

        AbstractSerialize serializeUtil = new HessianSerializeUtil();
        serializeUtil.serialize(future);
    }

    class AsyncFuture<T> extends CompletableFuture<T> implements Serializable {


    }

}

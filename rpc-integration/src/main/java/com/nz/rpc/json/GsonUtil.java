package com.nz.rpc.json;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Slf4j
@Component
public class GsonUtil implements Serializable {

    private static  Gson gson = new Gson();

    public static byte[] serialize(Object object,Class clz){
        byte[] result;
        String jsonStr =  gson.toJson(object);
        log.debug("jsonStr = " + jsonStr);
        return jsonStr.getBytes();

    }

    public static <T> T deserialize(byte[] body,Class<? extends T> clz){
        return gson.fromJson(new String(body),clz);
    }
}




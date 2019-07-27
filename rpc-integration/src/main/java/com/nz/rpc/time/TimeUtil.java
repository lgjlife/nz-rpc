package com.nz.rpc.time;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class TimeUtil {

    private static Map<String,Long> timeMap = new ConcurrentHashMap();

    public static void start(String name ,Long id){
        String key = name+id;
        timeMap.put(key,System.currentTimeMillis());
    }
    public static void endAndPrint(String name ,Long id){
        String key = name+id;
        if(timeMap.containsKey(key)){
            log.debug("{} time is {} ms ",key,System.currentTimeMillis() -timeMap.get(key));
            timeMap.remove(key);
        }
    }


}

package com.nz.rpc.provider;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ProviderHandle {

    private Map<String,String> clzMap = new HashMap<>();

    public void put(String interfaceName,String className){
        clzMap.put(interfaceName,className);

    }

    public void remove(String interfaceName){
        clzMap.remove(interfaceName);

    }
}

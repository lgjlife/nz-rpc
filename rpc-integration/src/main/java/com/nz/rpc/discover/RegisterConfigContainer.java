package com.nz.rpc.discover;

import java.util.HashMap;
import java.util.Map;


public class RegisterConfigContainer {

    private static  Map<String,RegistryConfig>  configMap = new HashMap<>();

    public static  void putConfig(RegistryConfig config){
        configMap.put(getKey(config),config);
    }


    public static Map<String, RegistryConfig> getConfigMap() {
        return configMap;
    }

    private static String getKey(RegistryConfig config){
        return  config.getApplication()+":"+config.getInterfaceName();
    }
}

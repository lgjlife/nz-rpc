package com.nz.rpc.discover;

import java.util.HashMap;
import java.util.Map;


public class ProviderConfigContainer {

    private static  Map<String, ProviderConfig>  configMap = new HashMap<>();

    public static  void putConfig(ProviderConfig config){
        configMap.put(getKey(config),config);
    }


    public static Map<String, ProviderConfig> getConfigMap() {
        return configMap;
    }

    private static String getKey(ProviderConfig config){
        return  config.getApplication()+":"+config.getInterfaceName();
    }
}

package com.nz.rpc.client.config;

import com.nz.rpc.rpcsupport.utils.RegistryConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class  ServiceConfig {

    private static List<RegistryConfig> configs;

    private static Map<String,List<RegistryConfig>>  services = new HashMap<>();


    public static void setConfigs(List<RegistryConfig> cfg) {
        configs = cfg;
    }

    public static List<RegistryConfig> getConfigs() {
        return configs;
    }

    private static String getKey(String applicationName,String interfaceName){

        return applicationName + ":" + interfaceName;

    }

    public static void updateServices(String  key, List<RegistryConfig>  configs) {

        ServiceConfig.services.put(key,configs);
    }


    public static void setServices(Map<String, List<RegistryConfig>> services) {
        ServiceConfig.services.clear();
        ServiceConfig.services = services;
    }

    public static Map<String, List<RegistryConfig>> getServices() {
        return services;
    }
}

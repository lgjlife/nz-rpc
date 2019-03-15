package com.nz.rpc.client.config;

import com.nz.rpc.rpcsupport.utils.RegistryConfig;

import java.util.List;


public class ServiceConfig {

    private static List<RegistryConfig> configs;

    public static void setConfigs(List<RegistryConfig> cfg) {
        configs = cfg;
    }

    public static List<RegistryConfig> getConfigs(List<RegistryConfig> cfg) {
        return configs;
    }


}

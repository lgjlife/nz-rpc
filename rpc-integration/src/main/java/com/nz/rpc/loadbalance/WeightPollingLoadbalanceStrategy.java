package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.RegistryConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡--加权轮询
 * @date 3/28/19
*/
public class WeightPollingLoadbalanceStrategy implements LoadbalanceStrategy {



    private Map<String,Integer> indexMap = new ConcurrentHashMap<>();

    public RegistryConfig select(List<RegistryConfig> configs){

        Integer index = indexMap.get(getKey(configs.get(0)));
        if(index == null){
            indexMap.put(getKey(configs.get(0)),0);
            return configs.get(0);
        }
        else {

            List<RegistryConfig> registryConfigs = new ArrayList<>();


            for(RegistryConfig config:configs){

                for(int i = 0; i< config.getWeight(); i++){
                    registryConfigs.add(config);
                }

            }

            index++;
            if(index >= registryConfigs.size()){
                index = 0;
            }
            indexMap.put(getKey(configs.get(0)),index);
            return registryConfigs.get(index);

        }
    }

    public String getKey(RegistryConfig config){

        return  config.getInterfaceName();
    }
}

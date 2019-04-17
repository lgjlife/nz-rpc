package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.RegistryConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡--轮询
 * @date 3/28/19
*/
public class PollingLoadbalanceStrategy implements LoadbalanceStrategy {

    private Map<String,Integer> indexMap = new ConcurrentHashMap<>();

    public RegistryConfig select(List<RegistryConfig> configs){

        Integer index = indexMap.get(getKey(configs.get(0)));
        if(index == null){
            indexMap.put(getKey(configs.get(0)),0);
            return configs.get(0);
        }
        else {
            index++;
            if(index >= configs.size()){
                index = 0;
            }
            indexMap.put(getKey(configs.get(0)),index);
            return configs.get(index);

        }
    }

    public String getKey(RegistryConfig config){

        return  config.getInterfaceName();
    }
}

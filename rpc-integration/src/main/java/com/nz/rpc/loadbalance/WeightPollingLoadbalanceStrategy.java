package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.loadbalance.exception.LoadbalanceException;

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

    public ProviderConfig select(List<ProviderConfig> configs, Object object)throws Exception{

        if(configs == null){
            throw  new NullPointerException();
        }
        if(configs.size() == 0){
            throw  new LoadbalanceException("Load balance fail! The configs have 0 config ");
        }

        Integer index = indexMap.get(getKey(configs.get(0)));
        if(index == null){
            indexMap.put(getKey(configs.get(0)),0);
            return configs.get(0);
        }
        else {

            List<ProviderConfig> newConfigs = new ArrayList<>();

            for(ProviderConfig config:configs){

                for(int i = 0; i< config.getWeight(); i++){
                    newConfigs.add(config);
                }
            }
            index++;
            if(index >= newConfigs.size()){
                index = 0;
            }
            indexMap.put(getKey(configs.get(0)),index);
            return newConfigs.get(index);

        }
    }

    public String getKey(ProviderConfig config){

        return  config.getInterfaceName();
    }
}

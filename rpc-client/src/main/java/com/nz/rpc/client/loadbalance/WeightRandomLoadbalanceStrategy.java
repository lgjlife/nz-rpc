package com.nz.rpc.client.loadbalance;

import com.nz.rpc.rpcsupport.utils.RegistryConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *功能描述 
 * @author lgj
 * @Description  随机负载均衡
 * @date 3/28/19
*/
public class WeightRandomLoadbalanceStrategy implements LoadbalanceStrategy{

    @Override
    public RegistryConfig select(List<RegistryConfig> configs) {

        List<RegistryConfig> registryConfigs = new ArrayList<>();


        for(RegistryConfig config:configs){

            for(int i = 0; i< config.getWeight(); i++){
                registryConfigs.add(config);
            }

        }

        int index = new Random().nextInt(registryConfigs.size()-1);
        return registryConfigs.get(index);
    }
}

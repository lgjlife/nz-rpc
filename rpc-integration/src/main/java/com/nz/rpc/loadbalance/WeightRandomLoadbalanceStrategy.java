package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *功能描述 
 * @author lgj
 * @Description  加权随机负载均衡
 * @date 3/28/19
*/
public class WeightRandomLoadbalanceStrategy implements LoadbalanceStrategy{

    @Override
    public ProviderConfig doSelect(List<ProviderConfig> configs, Object object)  {

        List<ProviderConfig> newConfigs = new ArrayList<>();

        for(ProviderConfig config:configs){

            for(int i = 0; i< config.getWeight(); i++){
                newConfigs.add(config);
            }

        }

        int index = new Random().nextInt(newConfigs.size()-1);
        return newConfigs.get(index);
    }
}

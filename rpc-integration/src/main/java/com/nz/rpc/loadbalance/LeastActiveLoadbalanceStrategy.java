package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡－最小调用时延
 * @date 3/28/19
*/
public class LeastActiveLoadbalanceStrategy implements  LoadbalanceStrategy{

    public ProviderConfig doSelect(List<ProviderConfig> configs, Object object){

        ProviderConfig[] registryConfigs= new ProviderConfig[configs.size()];
        configs.toArray(registryConfigs);

        Arrays.sort(registryConfigs, new Comparator<ProviderConfig>() {
            @Override
            public int compare(ProviderConfig o1, ProviderConfig o2) {

                if(o1.getCallTime() < o2.getCallTime()){
                    return -1;
                }

                else  if(o1.getCallTime() == o2.getCallTime()){
                    return 0;
                }
                else {
                    return 1;
                }
            }
        });

        return registryConfigs[0];
    }
}

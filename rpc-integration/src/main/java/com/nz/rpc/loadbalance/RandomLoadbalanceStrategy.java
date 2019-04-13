package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.RegistryConfig;

import java.util.List;
import java.util.Random;

/**
 *功能描述 
 * @author lgj
 * @Description  随机负载均衡
 * @date 3/28/19
*/
public class RandomLoadbalanceStrategy  implements LoadbalanceStrategy{

    @Override
    public RegistryConfig select(List<RegistryConfig> configs) {

        int index = new Random().nextInt(configs.size()-1);
        return configs.get(index);
    }
}

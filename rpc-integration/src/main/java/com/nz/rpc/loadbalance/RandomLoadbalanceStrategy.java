package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.loadbalance.exception.LoadbalanceException;

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
    public ProviderConfig select(List<ProviderConfig> configs, Object object) throws Exception{

        if(configs == null){
            throw  new NullPointerException();
        }
        if(configs.size() == 0){
            throw  new LoadbalanceException("Load balance fail! The configs have 0 config ");
        }

        int index = new Random().nextInt(configs.size());
        return configs.get(index);
    }


}

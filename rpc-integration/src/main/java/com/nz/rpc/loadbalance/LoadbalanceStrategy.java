package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;
import com.nz.rpc.loadbalance.exception.LoadbalanceException;

import java.util.List;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡接口
 * @date 3/28/19
*/
public interface LoadbalanceStrategy {

    default  public ProviderConfig select(List<ProviderConfig> configs, Object object) throws Exception{

        if((configs == null) || (configs.isEmpty())){
            throw new LoadbalanceException("Loadbalance fail!,No provider can select! ");
        }
        if(configs.size() == 1){
            return configs.get(0);
        }

        return doSelect(configs,object);

    }

    ProviderConfig doSelect(List<ProviderConfig> configs, Object object) ;

}

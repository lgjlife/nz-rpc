package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;

import java.util.List;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡接口
 * @date 3/28/19
*/
public interface LoadbalanceStrategy {

    public ProviderConfig select(List<ProviderConfig> configs, Object object) throws Exception;
}

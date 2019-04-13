package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.RegistryConfig;

import java.util.List;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡接口
 * @date 3/28/19
*/
public interface LoadbalanceStrategy {

    public RegistryConfig select(List<RegistryConfig> configs);
}

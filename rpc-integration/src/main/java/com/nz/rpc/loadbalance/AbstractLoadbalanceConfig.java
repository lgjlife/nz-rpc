package com.nz.rpc.loadbalance;

import java.util.Map;


/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡策略配置类
 *               客户端通过实现 该类配置每个RPC接口的负载均衡策略
 * @date 7/12/19
*/
public abstract class AbstractLoadbalanceConfig {

    /***
     *
     * va
     */
    /**
     *功能描述
     * @author lgj
     * @Description
     * @date 7/12/19
     * @param:  map-key:  接口的全限定名称 , map-value: LoadbalanceStrategy 实现类
     * @return:
     *
    */
    public abstract Map<String,Class<? extends LoadbalanceStrategy>> config();
}

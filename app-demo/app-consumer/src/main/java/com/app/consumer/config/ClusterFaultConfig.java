package com.app.consumer.config;

import com.app.common.service.DemoService;
import com.nz.rpc.cluster.AbstractClusterFaultConfig;
import com.nz.rpc.cluster.ClusterFault;
import com.nz.rpc.cluster.FailsaveClusterFault;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *功能描述 
 * @author lgj
 * @Description  集群容错配置
 * @date 7/12/19
*/
@Component
public class ClusterFaultConfig extends AbstractClusterFaultConfig {
    @Override
    public Map<String, Class<? extends ClusterFault>> config() {
        Map<String, Class<? extends ClusterFault>> configs = new HashMap<>();
        configs.put(DemoService.class.getName(), FailsaveClusterFault.class);
        return configs;
    }
}

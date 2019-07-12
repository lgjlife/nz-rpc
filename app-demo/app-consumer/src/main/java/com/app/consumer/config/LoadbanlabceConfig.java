package com.app.consumer.config;

import com.app.common.service.DemoService;
import com.nz.rpc.loadbalance.AbstractLoadbalanceConfig;
import com.nz.rpc.loadbalance.LoadbalanceStrategy;
import com.nz.rpc.loadbalance.PollingLoadbalanceStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoadbanlabceConfig extends AbstractLoadbalanceConfig {

    @Override
    public Map<String, Class<? extends LoadbalanceStrategy>> config() {
        Map<String, Class<? extends LoadbalanceStrategy>> configs = new HashMap<>();
        configs.put(DemoService.class.getName(), PollingLoadbalanceStrategy.class);

        return configs;
    }
}

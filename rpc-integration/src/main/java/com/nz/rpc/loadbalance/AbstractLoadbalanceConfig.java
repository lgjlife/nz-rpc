package com.nz.rpc.loadbalance;

import java.util.Map;

public abstract class AbstractLoadbalanceConfig {

    public abstract Map<String,Class<? extends LoadbalanceStrategy>> config();
}

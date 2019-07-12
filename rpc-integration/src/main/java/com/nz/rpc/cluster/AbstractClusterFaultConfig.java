package com.nz.rpc.cluster;

import java.util.Map;

public abstract class AbstractClusterFaultConfig {

    public abstract Map<String,Class<? extends ClusterFault>> config();

}

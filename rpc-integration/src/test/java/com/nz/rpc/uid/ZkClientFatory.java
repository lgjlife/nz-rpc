package com.nz.rpc.uid;

import com.nz.rpc.properties.RpcProperties;
import com.nz.rpc.zk.ZkCli;

public class ZkClientFatory {


    public static ZkCli zkCli(){
        ZkCli zkCli = new ZkCli();
        RpcProperties properties = new RpcProperties();
        properties.setZookeeperAdress("127.0.0.1:2181");
        zkCli.setProperties(properties);
        zkCli.connect();
        return zkCli;
    }

}

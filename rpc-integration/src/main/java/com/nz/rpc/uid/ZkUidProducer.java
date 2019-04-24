package com.nz.rpc.uid;

import com.nz.rpc.zk.ZkCli;
import com.nz.rpc.zk.ZkCreateConfig;
import org.apache.zookeeper.CreateMode;

public class ZkUidProducer implements UidProducer {

    private ZkCli zkCli;
    private  String uidPath = "/nzRpcUid";

    public ZkUidProducer(ZkCli zkCli) {

        this.zkCli = zkCli;
        createPath();

    }

    @Override
    public long getUid() {

        return zkCli.getVersion(uidPath);
    }

    private void createPath(){

        ZkCreateConfig zkCreateConfig = ZkCreateConfig
                .builder()
                .path(uidPath)
                .createMode(CreateMode.PERSISTENT)
                .build();
        zkCli.createPath(zkCreateConfig);
    }



}

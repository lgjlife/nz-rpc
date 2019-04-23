package com.nz.rpc.utils.uid;

import com.nz.rpc.zk.ZkCli;
import com.nz.rpc.zk.ZkCreateConfig;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class ZkUidProducer implements UidProducer {

    private ZkCli zkCli;
    private  String uidPath = "/nzRpcUid";

    public ZkUidProducer(ZkCli zkCli) {

        this.zkCli = zkCli;
    }

    @Override
    public long getUid() {

        Stat stat = zkCli.setData(uidPath,1);
        if (stat != null){

            return stat.getVersion();
        }
        else{
            createPath();
        }
        return 0;
    }

    private void createPath(){

        ZkCreateConfig zkCreateConfig = ZkCreateConfig
                .builder()
                .path(uidPath)
                .createMode(CreateMode.EPHEMERAL)
                .build();
        zkCli.createPath(zkCreateConfig);
    }



}

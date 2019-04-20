package com.nz.rpc.lock.zk;

import com.nz.rpc.lock.LockProperties;

public class ZkClientFatory {

    public static LockProperties lockProperties(){
        LockProperties lockProperties = new LockProperties();
        lockProperties.setZookeeperAdress("localhost:2181");

        return lockProperties;
    }

    public static ZkClient zkClient(){

        ZkClient zkClient = new ZkClient();
        zkClient.setProperties(lockProperties());
        zkClient.connect();
        return zkClient;
    }
    public static ZkLockUtil zkLockUtil(){
        ZkLockUtil zkLockUtil = new ZkLockUtil();
        zkLockUtil.setZkClient(zkClient());

        return zkLockUtil;
    }


}

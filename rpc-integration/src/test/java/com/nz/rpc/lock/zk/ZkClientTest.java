package com.nz.rpc.lock.zk;

import com.nz.rpc.zk.ZkCreateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;


@Slf4j
public class ZkClientTest {


    @Test
    public void zkcli(){

        ZkClient zkClient = ZkClientFatory.zkClient();

        ZkCreateConfig config =  ZkCreateConfig.builder()
                .createMode(CreateMode.EPHEMERAL)
                .path("/node/demo2")
                .build();

        String result = zkClient.createPath(config);





        log.info("创建结果：{}",result);

    }
}
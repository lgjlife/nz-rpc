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
                .createMode(CreateMode.PERSISTENT)
                .path("/node/demo")
                .build();

        String result = zkClient.createPath(config);
        try{

            while (true){
              //  Thread.sleep(10000);
             //   Stat stat = zkClient.setData("/node/demo","aa"+new Random().nextInt(100));
             //   System.out.println(stat);
             //   Object object = zkClient.getData("/node/demo",String.class);
             //   System.out.println(object);

            }
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }



        log.info("创建结果：{}",result);

    }
}
package com.nz.rpc.loadbalance;

import com.nz.rpc.discover.RegistryConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoadbalanceStrategyTest {

    @Test
    public void loadbalace(){
        System.out.println("随机负载均衡....");
        LoadbalanceStrategy strategy1 = new RandomLoadbalanceStrategy();
        LoadbalanceStrategy strategy2 = new WeightRandomLoadbalanceStrategy();
        randomLoadbalace(strategy1,10,1000);

        System.out.println("\r\n轮询负载均衡.....");
        LoadbalanceStrategy strategy3 = new PollingLoadbalanceStrategy();
        LoadbalanceStrategy strategy4 = new WeightPollingLoadbalanceStrategy();
        randomLoadbalace(strategy4,10,1000);

    }

    public void randomLoadbalace(LoadbalanceStrategy strategy ,int configNum,int testCount ){

        List<RegistryConfig> configs = new ArrayList<>();
        int[] counts = new int[configNum];


        for(int i = 0; i< configNum; i++){
            RegistryConfig config = new RegistryConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(i);
            config.setWeight(i);
            configs.add(config);
        }

        //System.out.println(configs);

        for(int i = 0; i< testCount ; i++){
            RegistryConfig config = strategy.select(configs);
           // System.out.println("选中的:"+config);
            Integer count = counts[config.getPort()];
            counts[config.getPort()] = ++count;

        }

        for(int i = 0; i< configNum; i++){
            System.out.println("序号:" + i + "--次数：" + counts[i]);
        }

    }

    public void pollingLoadbalace(LoadbalanceStrategy strategy ,int configNum,int testCount ){

        List<RegistryConfig> configs = new ArrayList<>();
        int[] counts = new int[configNum];


        for(int i = 0; i< configNum; i++){
            RegistryConfig config = new RegistryConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(i);
            config.setWeight(i);
            configs.add(config);
        }

        //System.out.println(configs);

        for(int i = 0; i< testCount ; i++){
            RegistryConfig config = strategy.select(configs);
            // System.out.println("选中的:"+config);
            Integer count = counts[config.getPort()];
            counts[config.getPort()] = ++count;

        }

        for(int i = 0; i< configNum; i++){
            System.out.println("序号:" + i + "--次数：" + counts[i]);
        }

    }


}
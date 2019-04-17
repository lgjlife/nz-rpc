package com.nz.rpc.loadbalance;

import com.nz.rpc.discover.RegistryConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadbalanceStrategyTest {

    @Test
    public void loadbalace(){
       /* System.out.println("随机负载均衡....");
        LoadbalanceStrategy strategy1 = new RandomLoadbalanceStrategy();
        LoadbalanceStrategy strategy2 = new WeightRandomLoadbalanceStrategy();
        loadbalace(strategy2,10,1000);

        System.out.println("\r\n轮询负载均衡.....");
        LoadbalanceStrategy strategy3 = new PollingLoadbalanceStrategy();
        LoadbalanceStrategy strategy4 = new WeightPollingLoadbalanceStrategy();
        loadbalace(strategy4,10,1000);*/


        System.out.println("\r\n最小时延负载均衡.....");
        LoadbalanceStrategy strategy5 = new LeastActiveLoadbalanceStrategy();
        leastActiveLoadbalance(strategy5,10);



    }

    public void loadbalace(LoadbalanceStrategy strategy ,int configNum,int testCount ){

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

    public void leastActiveLoadbalance(LoadbalanceStrategy strategy ,int configNum){

        List<RegistryConfig> configs = new ArrayList<>();
        int[] counts = new int[configNum];


        for(int i = 0; i< configNum; i++){
            RegistryConfig config = new RegistryConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(i);
            config.setWeight(i);
            config.setCallTime(new Random().nextInt(100));
            configs.add(config);
        }

        for(RegistryConfig c:configs){
            System.out.println("序号:" + c.getPort()  +"--时延:" + c.getCallTime() );
        }
        System.out.println("--------------");
        RegistryConfig config = strategy.select(configs);
        System.out.println("最终选择　序号:" + config.getPort()  +"--时延:" + config.getCallTime() );


    }



}
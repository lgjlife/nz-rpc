package com.nz.rpc.loadbalance;

import com.nz.rpc.discover.ProviderConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LoadbalanceStrategyTest {

    @Test
    public void loadbalace(){
        if(false){
            System.out.println("随机负载均衡....");
            LoadbalanceStrategy strategy1 = new RandomLoadbalanceStrategy();
            loadbalace(strategy1,10,1000);
        }
        if(false){
            System.out.println("加权随机负载均衡....");
            LoadbalanceStrategy strategy2 = new WeightRandomLoadbalanceStrategy();
            loadbalace(strategy2,10,1000);
        }

        if(false){
            System.out.println("\r\n轮询负载均衡.....");
            LoadbalanceStrategy strategy3 = new PollingLoadbalanceStrategy();
            loadbalace(strategy3,10,1000);
        }

        if(false){
            System.out.println("\r\n加权轮询负载均衡.....");
            LoadbalanceStrategy strategy4 = new WeightPollingLoadbalanceStrategy();
            loadbalace(strategy4,10,1000);
        }

        if(false){
            System.out.println("\r\n最小时延负载均衡.....");
            LoadbalanceStrategy strategy5 = new LeastActiveLoadbalanceStrategy();
            leastActiveLoadbalance(strategy5,10);
        }

        if(true){
            System.out.println("\r\n一致性hash负载均衡.....");
            uniformityHashLoadbalanceStrategyTest(new UniformityHashLoadbalanceStrategy(),10);

        }


    }

    public void uniformityHashLoadbalanceStrategyTest(LoadbalanceStrategy strategy ,int configNum){

        List<ProviderConfig> configs = new ArrayList<>();
        for(int i = 0; i< configNum; i++){
            ProviderConfig config = new ProviderConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(new Random().nextInt(9999));
            config.setWeight(i);
            config.setCallTime(new Random().nextInt(100));
            configs.add(config);
        }

        ProviderConfig config = strategy.select(configs,"127.0.0.1:0234");
        System.out.println("选择结果:" + config.getHost() + ":" + config.getPort());
    }

    public  void hashTest(int count){



        int[] result = new int[count];

        for(int i = 0; i< count; i++){
            String str = "127.0.0.1"+":"+ new Random().nextInt(9999);
            result[i] = caculHash(str);
        }
        Arrays.sort(result);

        for(int i = 0; i< count; i++){
            System.out.println(result[i]);

        }

        System.out.println();
        System.out.println(Integer.MAX_VALUE);

    }

    private int caculHash(String str){

       int hashCode =  str.hashCode();
        hashCode = (hashCode<0)?(-hashCode):hashCode;
        return hashCode;

    }

    private  int getHash(String str)
     {
         final int p = 16777619;
         int hash = (int)2166136261L;
         for (int i = 0; i < str.length(); i++)
                 hash = (hash ^ str.charAt(i)) * p;
         hash += hash << 13;
         hash ^= hash >> 7;
         hash += hash << 3;
         hash ^= hash >> 17;
         hash += hash << 5;

         // 如果算出来的值为负数则取其绝对值
         if (hash < 0)
                 hash = Math.abs(hash);
         return hash;
     }


    public void loadbalace(LoadbalanceStrategy strategy ,int configNum,int testCount ){

        List<ProviderConfig> configs = new ArrayList<>();
        int[] counts = new int[configNum];


        for(int i = 0; i< configNum; i++){
            ProviderConfig config = new ProviderConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(i);
            config.setWeight(new Random().nextInt(100));
            configs.add(config);
        }

        //System.out.println(configs);

        for(int i = 0; i< testCount ; i++){
            ProviderConfig config = strategy.select(configs,null);
           // System.out.println("选中的:"+config);
            Integer count = counts[config.getPort()];
            counts[config.getPort()] = ++count;

        }

        for(int i = 0; i< configNum; i++){
            System.out.println("序号:" + i + " 权重：" + configs.get(i).getWeight() + "--次数：" + counts[i]);
        }

    }

    public void leastActiveLoadbalance(LoadbalanceStrategy strategy ,int configNum){

        List<ProviderConfig> configs = new ArrayList<>();

        for(int i = 0; i< configNum; i++){
            ProviderConfig config = new ProviderConfig();
            config.setInterfaceName("com.serviceImpl");
            config.setHost("127.0.0.1");
            config.setPort(i);
            config.setWeight(i);
            config.setCallTime(new Random().nextInt(100));
            configs.add(config);
        }

        for(ProviderConfig c:configs){
            System.out.println("序号:" + c.getPort()  +"--时延:" + c.getCallTime() );
        }
        System.out.println("--------------");
        ProviderConfig config = strategy.select(configs,null);
        System.out.println("最终选择　序号:" + config.getPort()  +"--时延:" + config.getCallTime() );


    }





}
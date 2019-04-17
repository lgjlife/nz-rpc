package com.nz.rpc.loadbalance;


import com.nz.rpc.discover.ProviderConfig;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡接口
 * @date 3/28/19
*/
public class UniformityHashLoadbalanceStrategy  implements  LoadbalanceStrategy{

    private static final int VIRTUAL_NODES = 5;


    public ProviderConfig select(List<ProviderConfig> configs, Object object){

        SortedMap<Integer, ProviderConfig> sortedMap = new TreeMap();

        for(ProviderConfig config:configs){
            for(int j = 0; j < VIRTUAL_NODES; j++){
                sortedMap.put(caculHash(getKey(config.getHost(),config.getPort(),"&&node"+j)),config);
            }
        }

        System.out.println(sortedMap);
        Integer requestHashcCode = caculHash((String)object);
        sortedMap.forEach((k,v)->{
            System.out.println("hashcode: " + k + "  " + v.getHost()+":"+v.getPort());
        });
        System.out.println("------------------请求的hashcode:"+requestHashcCode);

        SortedMap<Integer, ProviderConfig> subMap = sortedMap.tailMap(requestHashcCode);
        Integer index = subMap.firstKey();


        return  subMap.get(index);
    }
    private String getKey(String host,int port,String node){
        return new StringBuilder().append(host).append(":").append(port).append(node).toString();
    }

    private int caculHash(String str){

       /* int hashCode =  str.hashCode();
        hashCode = (hashCode<0)?(-hashCode):hashCode;
        return hashCode;*/

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

}

package com.nz.rpc.loadbalance;

/**
 *功能描述 
 * @author lgj
 * @Description  负载均衡类型
 * @date 7/12/19
*/
public class LoadbalanceType {

    //轮询
    public static  final  String  POLLING_LOADBALANCE = "polling";
    //轮询权重
    public static  final  String  POLLING_WEIGHT_LOADBALANCE = "pollingWeight";


    //随机
    public static  final  String  RANDOM_LOADBALANCE = "random";
    //随机权重
    public static  final  String  RANDOM_WEIGHT_LOADBALANCE = "pollingWeight";

    //一致性Hash
    public static  final  String  UNIFORMITY_HASH_LOADBALANCE = "UniformityHash";




}

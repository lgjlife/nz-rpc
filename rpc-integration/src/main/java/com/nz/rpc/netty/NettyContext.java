package com.nz.rpc.netty;

import com.nz.rpc.netty.client.NettyClient;
import com.nz.rpc.netty.exception.RpcResponseException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class NettyContext {

    //netty操作客户端
    private static NettyClient nettyClient;

    //存放接口名称对应的实现类名称 key:接口名称，value:实现类名称
    private static Map<String,List<String>> localServiceImplMap = new ConcurrentHashMap<>();



    public static NettyClient getNettyClient() {
        return nettyClient;
    }

    public static void setNettyClient(NettyClient nettyClient) {
        NettyContext.nettyClient = nettyClient;
    }


    public static Map<String, List<String>> getLocalServiceImplMap() {
        return localServiceImplMap;
    }

    /**
     *功能描述
     * @author lgj
     * @Description  通过接口名称获取实现类
     * @date 7/13/19
     * @param:
     * @return:
     *
    */
    public static String getLocalServiceImpl(String interfaceName)throws Exception{

        List<String> serviceImplLists = NettyContext.getLocalServiceImplMap().get(interfaceName);
        if (serviceImplLists!= null){
            //随机获取实现类
            return serviceImplLists.get(new Random().nextInt(serviceImplLists.size()));
        }
        throw new RpcResponseException("The interface[{}] can not find its implementation class");

    }

    public static void setLocalServiceImplMap(Map<String, List<String>> localServiceImplMap) {
        NettyContext.localServiceImplMap = localServiceImplMap;
    }
}

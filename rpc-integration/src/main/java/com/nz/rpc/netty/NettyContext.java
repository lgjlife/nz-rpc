package com.nz.rpc.netty;

import com.nz.rpc.netty.client.NettyClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NettyContext {

    //private static AbstractSerialize serialize ;

    private static NettyClient nettyClient;

    private static Map<String,String> localServiceImplMap = new ConcurrentHashMap<>();



    /*public static AbstractSerialize getSerialize() {
        return serialize;
    }
*/
    /*public static void setSerialize(AbstractSerialize serialize) {
        NettyContext.serialize = serialize;
    }*/

    public static NettyClient getNettyClient() {
        return nettyClient;
    }

    public static void setNettyClient(NettyClient nettyClient) {
        NettyContext.nettyClient = nettyClient;
    }


    public static Map<String, String> getLocalServiceImplMap() {
        return localServiceImplMap;
    }

    public static void setLocalServiceImplMap(Map<String, String> localServiceImplMap) {
        NettyContext.localServiceImplMap = localServiceImplMap;
    }
}

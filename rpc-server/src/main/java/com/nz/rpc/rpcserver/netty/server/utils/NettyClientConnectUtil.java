package com.nz.rpc.rpcserver.netty.server.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyClientConnectUtil {

    private static AtomicInteger connectCounter = new AtomicInteger(0);


    public static void addConnectCounter() {
        connectCounter.incrementAndGet();
    }

    public static void decConnectCounter() {
        connectCounter.decrementAndGet();
    }

    public static int getConnectCounter() {
        return connectCounter.get();
    }


}

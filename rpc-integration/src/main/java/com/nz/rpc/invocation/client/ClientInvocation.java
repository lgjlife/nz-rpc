package com.nz.rpc.invocation.client;

import java.lang.reflect.Method;

public interface ClientInvocation {

    Method getMethod();
    Object[] getArgs();
    Object executeNext() throws Exception;
}

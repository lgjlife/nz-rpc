package com.nz.rpc.invocation.client;

import java.lang.reflect.Method;
import java.util.Map;

public interface ClientInvocation {

    Method getMethod();
    Object[] getArgs();
    Object executeNext() throws RuntimeException;
    Map<String, String> getAttachments();

}

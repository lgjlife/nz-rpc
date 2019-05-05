package com.nz.rpc.interceptor;

import com.nz.rpc.invocation.client.ClientInvocation;

public interface Interceptor {

    Object intercept(ClientInvocation invocation) throws Exception;

}

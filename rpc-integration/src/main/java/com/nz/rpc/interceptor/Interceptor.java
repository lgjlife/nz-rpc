package com.nz.rpc.interceptor;

import com.nz.rpc.invocation.client.ClientInvocation;

/**
 *功能描述
 * @author lgj
 * @Description  责任链接口
 * @date 5/6/19
*/
public interface Interceptor {

    Object intercept(ClientInvocation invocation) throws Exception;

}

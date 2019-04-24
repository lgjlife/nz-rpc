package com.nz.rpc.msg.request;

import java.lang.reflect.Method;

public interface RequestHandler {

    Object invoke(Method method, Object[] args);

}

package com.nz.rpc.rpcserver.handle.requret;

import com.nz.rpc.rpcsupport.utils.RpcRequest;
import com.nz.rpc.rpcsupport.utils.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
public class ClientRequestHandler {


    public RpcResponse handler(RpcRequest request) {

        log.debug("ClientRequestHandler handler");
        RpcResponse response = new RpcResponse();
        String requestId = request.getRequestId(); //请求id
        String className = request.getClassName();  //调用类名
        String methodName = request.getMethodName(); //调用方法名
        Class<?>[] parameterTypes = request.getParameterTypes(); //方法参数类型
        Object[] parameters = request.getParameters();   //方法参数


        log.debug("method = " + methodName);
        try {
            Class<?> clazz = Class.forName(className);
            Object target = clazz.newInstance();
            log.debug("clazz = {},methodName  = {}, parameterTypes = {} ",clazz,methodName,parameterTypes[0]);
            Method method = target.getClass().getDeclaredMethod(methodName, parameterTypes);

            Object result = method.invoke(target, parameters);
            response.setResult(result);
        } catch (Exception ex) {
            log.error("ClientRequestHangler error ! " + ex);
            ex.printStackTrace();
            response.setException(ex);
        }

        return response;

    }

    public static void main(String args[]) {
        RpcRequest request = new RpcRequest();

        /*request.setClassName("com.app.common.IUserService");
        request.setMethodName("queryName");
        String[] as = new String[1];
        as[0] = "asaddsasad";
        Class<?>[] types = new Class[1];
        types[0] = as[0].getClass();
        request.setParameterTypes(types);
        request.setParameters(as);*/


        try{
            Class<?> clazz = Class.forName("com.app.common.IUserService");
            Object target = clazz.newInstance();
            Method method = target.getClass().getDeclaredMethod("queryName", String.class);

          //  Object result = method.invoke(target, parameters);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }



        //   RpcResponse response = handler(request);

        // System.out.println(response.getResult());
    }

}

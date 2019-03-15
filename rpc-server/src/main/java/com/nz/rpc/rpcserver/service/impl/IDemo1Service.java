package com.nz.rpc.rpcserver.service.impl;


import com.nz.rpc.rpcserver.service.Demo1Service;
import com.nz.rpc.rpcsupport.annotation.RpcService;
import org.springframework.stereotype.Component;

@Component
@RpcService
public class IDemo1Service implements Demo1Service {

    @Override
    public void func1() {
        System.out.println("IDemo1Service   func1");
    }

    @Override
    public void func2() {
        System.out.println("IDemo1Service   func2");
    }

    @Override
    public String func3(Integer num) {
        return "测试数据： " + num;
    }

    @Override
    public String func4(String num) {
        return "测试数据： " + num;
    }


}

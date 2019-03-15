package com.nz.rpc.rpcserver.service.impl;


import com.nz.rpc.rpcserver.service.Demo2Service;
import com.nz.rpc.rpcsupport.annotation.RpcService;
import org.springframework.stereotype.Component;

@Component
@RpcService
public class IDemo2Service implements Demo2Service {
    @Override
    public void func1() {

    }

    @Override
    public void func2() {

    }
}

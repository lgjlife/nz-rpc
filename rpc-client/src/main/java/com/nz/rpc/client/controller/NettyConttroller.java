package com.nz.rpc.client.controller;


import com.nz.rpc.client.proxy.Demo;
import com.nz.rpc.rpcsupport.annotation.RpcReference;
import com.nz.rpc.rpcsupport.service.RpcDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/rpc")
@RestController
public class NettyConttroller {


    @Autowired
    ApplicationContext context;

    @Autowired
    @RpcReference
    public RpcDemoService rpcDemoService;

    @Autowired
    Demo demo;


    @RequestMapping("/data")
    public String sayHello(String data) {


        String result =  demo.func();
        System.out.println("result = " + result);

       // return result;

       /* RpcDemoService rpcDemoService1 =context.getBean(RpcDemoService.class);
        if(rpcDemoService == null){
            log.debug("rpcDemoService1 is null");

        }
        else {
            log.debug("rpcDemoService1  sayHello");

        }*/

        if(rpcDemoService == null){
            log.debug("rpcDemoService is null");
            return  "rpcDemoService is null";
        }
        else {
            log.debug("NettyConttroller  sayHello");
            return  rpcDemoService.func1("sadasDSDSADSADsDFSAFDFDA");
        }

    }
}

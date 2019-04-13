package com.app.consumer.controller;


import com.app.common.service.DemoService;
import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.proxy.RpcProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {


    private String data = "122";
    @RpcReference
    private UserService userService;

    @RpcReference
    private DemoService demoService;



    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName("qqwq",13546L);
        demoService.setName(13546L);


    }

    @GetMapping("/demo1")
    public  void  demo1(){
        log.debug("/demo1");
        UserService userService =  new RpcProxyFactory().createInstance(UserService.class);
        String result =  userService.queryName("aaa",123L);
        log.debug("result = " + result);


    }




}

package com.app.consumer.controller;


import com.app.common.service.DemoService;
import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.utils.uid.ZkUidProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private ZkUidProducer zkUidProducer;


    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName("qqwq",13546L);
        demoService.setName(13546L);


    }

    @GetMapping("/uid")
    public  Integer  demo1(){
       return zkUidProducer.getUidForInt();

    }




}

class UserSemo implements  UserService{
    @Override
    public String queryName(Long id) {
        return null;
    }

    @Override
    public String queryName(String name, Long id) {
        return null;
    }
}
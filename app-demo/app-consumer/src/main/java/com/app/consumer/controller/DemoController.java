package com.app.consumer.controller;


import com.app.common.service.DemoService;
import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.uid.UidProducer;
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
    private UidProducer uidProducer;

    @GetMapping("/demo")
    public  String  demo(){
        log.debug("/demo");
        String reslut = userService.queryName("qqwq",13546L);
        System.out.println(reslut);
        reslut = demoService.setName(13546L);
        System.out.println(reslut);
        return reslut;



    }

    @GetMapping("/uid")
    public  String  demo1(){
        long zkCount = uidProducer.getUid();
        long customCount = uidProducer.getUid();

       return "zkCount: " + zkCount + "  " + "customCount : " + customCount;


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
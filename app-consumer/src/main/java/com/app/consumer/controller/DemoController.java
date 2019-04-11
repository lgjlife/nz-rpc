package com.app.consumer.controller;


import com.app.common.service.DemoService;
import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {


    @RpcReference
    private UserService userService;

    @RpcReference
    private DemoService demoService;



    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName(13546L);
        demoService.setName(13546L);


    }


}

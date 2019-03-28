package com.app.consumer.controller;

import com.app.common.service.IDemoService;
import com.app.common.service.IUserService;
import com.nz.rpc.rpcsupport.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {


    @RpcReference
    private IUserService userService;

    @RpcReference
    private IDemoService demoService;



    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName(13546L);
        demoService.setName(13546L);


    }


}

package com.demo.dubboconsumer.controller;


import com.app.common.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {

    @Reference
    private DemoService demoService;


    @GetMapping("/demo")
    public  String  demo(){
        log.debug("/demo");
        String reslut = demoService.setName(13546L);

        log.debug(reslut);
        return reslut;
    }

}

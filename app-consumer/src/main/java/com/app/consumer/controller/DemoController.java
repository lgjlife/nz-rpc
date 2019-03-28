package com.app.consumer.controller;

import com.app.common.service.IUserService;
import com.nz.rpc.rpcsupport.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
@RestController
public class DemoController {

  //  @Autowired
    @RpcReference
    private IUserService userService;

    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName(13546L);

    }

    public static void main(String args[]){
        Reflections reflections1 = new Reflections("com.app.*",new FieldAnnotationsScanner());
        Set<Field> serviceNames = reflections1.getFieldsAnnotatedWith(RpcReference.class);

        System.out.println();
        System.out.println(serviceNames);
    }
}

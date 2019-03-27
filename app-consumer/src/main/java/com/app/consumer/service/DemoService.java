package com.app.consumer.service;

import com.app.common.service.IUserService;
import com.nz.rpc.rpcsupport.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoService {

   // @Autowired
    @RpcReference
    private IUserService userService;

    @GetMapping("/demo")
    public  void  demo(){
        log.debug("/demo");
        userService.queryName(13546L);

    }
}

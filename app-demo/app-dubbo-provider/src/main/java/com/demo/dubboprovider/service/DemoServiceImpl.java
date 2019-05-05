package com.demo.dubboprovider.service;


import com.app.common.service.DemoService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Service
@Component
public class DemoServiceImpl implements DemoService {


    public  String setName(Long id){
        return  "DemoServiceImpl  setName :" +id + ":"+ new Date().toString()+ "   " + new Random().nextInt(1000);
    }

    public  String setName(String name, Long id){
        return  "DemoServiceImpl  queryName :" + name +":"
                +id
                + new Date().toString()
                + "   " + new Random().nextInt(1000);
    }
}

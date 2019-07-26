package com.app.consumer.controller;


import com.app.common.service.DemoService;
import com.app.common.service.UserService;
import com.nz.rpc.anno.RpcReference;
import com.nz.rpc.uid.UidProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class DemoController {

    private AtomicInteger allCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);



    @RpcReference
    private UserService userService;

    @RpcReference
    private DemoService demoService;


    @Autowired
    private UidProducer uidProducer;


    @RequestMapping("/reset")
    public void reset(){
        allCount.set(0);
        successCount.set(0);
        failCount.set(0);
    }

    @GetMapping("/demo")
    public  String  demo(){

        log.debug("/demo");

        String reslut = null;

        long startTime = System.currentTimeMillis();
        reslut = demoService.setName(13546L);
        long endTime = System.currentTimeMillis();
        if(reslut != null){
            allCount.incrementAndGet();
            successCount.incrementAndGet();
        }
        else {
            allCount.incrementAndGet();
        }
        //log.debug(reslut);

        int fail = allCount.get() - successCount.get();
        double percent = (successCount.get()*1.0*100/allCount.get());
        reslut = "allCount=" + allCount + ","
                +  " successCount=" + successCount + ","
                +  " failCount=" + (fail) + ",   "
                + String.format("%.2f",percent)+"%"
                + "  time = "+ (endTime-startTime);

        log.debug("DemoController reslut = " + reslut);
        return reslut;



    }



    @GetMapping("/uid")
    public  String  demo1(){
        long zkCount = uidProducer.getUid();
        long customCount = uidProducer.getUid();

       return "zkCount: " + zkCount + "  " + "customCount : " + customCount;


    }



    public static void main(String args[]){
        System.out.println( System.getProperty("os.name"));
    }

}

/*
class UserSemo implements  UserService{
    @Override
    public String queryName(Long id) {
        return null;
    }

    @Override
    public String queryName(String name, Long id) {
        return null;
    }
}*/

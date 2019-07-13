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
        System.out.println(String.format("%.2f",145.23456));
    }



    /*******************************************************************************
     * Function Name  : calc_average
     * Description    : 计算平均值 公式: avg=last_avg*((num-1)/num)+next_val*1/num
     * Input          : last_avg:上一次平均值 next_val:加入计算的值 num:到现在加入计算的总个数
     * Output         : None
     * Return         : 平均值
     *******************************************************************************/
    static float calc_average(float last_avg, float next_val, int num)
    {
        float avg_val=0;
        if(num<=1)
        {
            avg_val=next_val;
        }
        else
        {
            avg_val=(last_avg*(((float)num-1)/(float)num)+next_val*(1/(float)num));/*必须强转float*/
        }
        return avg_val;
    }
    static  float func3(int[] arr){

        float avr = 0;

        for(int i = 0; i< arr.length; i++){

            avr = calc_average(avr,arr[i],i);

        }

        return avr;

    }

    static  float func2(int[] arr){
        float avr = 0;

        for(int i = 0; i< arr.length; i++){

            avr =(avr+arr[i])/2;

        }

        return avr;
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

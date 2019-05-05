package com.demo.test.service;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Data
public class TestRequest {


    private Map<String,String> urlMap = new LinkedHashMap<>();
    private int defaultTestNums = 1;
    private int nums = defaultTestNums;

    private Map<String,TreeSet<Long>> takeTimeMap = new LinkedHashMap<>();

    public TreeSet startTest(String type){

        AtomicInteger successCount =new AtomicInteger(0);

        RestTemplate restTemplate = new RestTemplate();

        successCount.set(0);
        String testNums = System.getProperty("test.nums");
        log.info("testNums:{}",testNums);


        if(testNums != null){
             nums = Integer.valueOf(testNums);
        }

        TimeRecord timeRecord =  new TimeRecord();


        CyclicBarrier startCyclicBarrier = new CyclicBarrier(nums,new StartTask(timeRecord));
        CyclicBarrier endCyclicBarrier = new CyclicBarrier(nums,new EndTask(timeRecord,successCount,type));

        for(int i = 0; i< nums; i++){

            new Thread(){

                @Override
                public void run() {
                    try{

                        startCyclicBarrier.await();
                        String result = restTemplate.getForObject(urlMap.get(type),String.class);
                        if(result != null){
                            successCount.incrementAndGet();
                        }
                        endCyclicBarrier.await();



                    }
                    catch(Exception ex){
                        log.error(ex.getMessage());
                        try{
                            endCyclicBarrier.await();
                        }
                        catch(Exception e){
                            log.error(e.getMessage());
                        }
                    }
                    //System.out.println("back out thread!");
                }
            }.start();
        }
        String result = "";
        if(takeTimeMap.get(type).size()>0){
            result = "first:"+takeTimeMap.get(type).first() + " last:" + takeTimeMap.get(type).last();

        }

        return takeTimeMap.get(type);
    }


    @PostConstruct
    public void init(){
        urlMap.put("nzrpc","http://localhost:8112/demo");
        urlMap.put("dubbo","http://localhost:8583/demo");
        takeTimeMap.put("nzrpc",new TreeSet<Long>());
        takeTimeMap.put("dubbo",new TreeSet<Long>());
        //startTest("nzrpc");
    }

    @Data
    class TimeRecord{

        private Long startTime = 0L;
        private Long endTime = 0L;
    }


    class StartTask implements Runnable{

        private TimeRecord timeRecord;

        public StartTask(TimeRecord timeRecord) {
            this.timeRecord = timeRecord;
        }

        @Override
        public void run() {
            log.info("start request.........");
            timeRecord.startTime = System.currentTimeMillis();
        }
    }

    class EndTask implements Runnable{

        private TimeRecord timeRecord;
        private  AtomicInteger successCount;
        private String type;


        public EndTask(TimeRecord timeRecord, AtomicInteger successCount, String type) {
            this.timeRecord = timeRecord;
            this.successCount = successCount;
            this.type = type;
        }

        @Override
        public void run() {
            long diff = System.currentTimeMillis() - timeRecord.getStartTime();

            takeTimeMap.get(type).add(diff);

            log.info("end request.........");
            log.info("successCount = " + successCount.get());
            log.info("All the request [{}] take time : :[{}]",nums,diff);
        }
    }







}

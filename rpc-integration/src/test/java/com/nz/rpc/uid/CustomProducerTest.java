package com.nz.rpc.uid;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class CustomProducerTest {


    ReentrantLock lock = new ReentrantLock();
    int iMax = 100;
    int jMax = 100;
    Long[] nums= new Long[iMax * jMax];
    List<Long> list = new LinkedList<>();

    Map<Long,Object> map = new ConcurrentHashMap<>();


    /**
     *功能描述
     * @author lgj
     * @Description
     * 1000 ----> 17ms
     * 10000  --->34
     * 100000---> 54
     * 1000000--->157
     * 10000000---> 1077
     * @date 4/23/19
     * @param:
     * @return:
     *
    */
    @Test
    public  void time(){

        UidProducer producer = new CustomProducer(100);

        int count = 0;
        int max = 1000000;
        long start = System.currentTimeMillis();

        while (count++<max){
            long uid = producer.getUid();
        }

        long end = System.currentTimeMillis();

        System.out.println(count + "--->" + (end-start));

    }
    @Test
    public void getUidForLong() {

        int nThread = 10000;
        CountDownLatch latch = new CountDownLatch(nThread);

        UidProducer producer = new CustomProducer(100);

        for(int i = 0; i< nThread; i++){

            new Thread(()->{

                for(int j = 0; j < 100; j++){

                    long uid = producer.getUid();
                   // System.out.println(uid);
                   //add(uid);
                   map.put(uid,new Object());
                }
                latch.countDown();

            }).start();
        }
        try{

            latch.await();
        }
        catch(Exception ex){

        }

        System.out.println("长度:"+map.size());

        Object[] array = list.toArray();
        Arrays.sort(array);

        System.out.println("长度:"+array.length);
        for(Object v:array){

            //System.out.println((Long)v);

        }





    }

    public void add(Long num){


        try{

            lock.lock();
            list.add(num);
        }
        catch(Exception ex){

        }
        finally{

            lock.unlock();
        }
    }
}
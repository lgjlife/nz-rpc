package com.nz.rpc.utils.uid;

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


    @Test
    public void getUidForLong() {

        int nThread = 100;
        CountDownLatch latch = new CountDownLatch(nThread);

        UidProducer producer = new CustomProducer(100);

        for(int i = 0; i< nThread; i++){

            new Thread(()->{

                for(int j = 0; j < 100; j++){

                    long uid = producer.getUid();
                   // System.out.println(uid);
                   add(uid);
                    //map.put(uid,new Object());
                }
                latch.countDown();

            }).start();
        }
        try{

            latch.await();
        }
        catch(Exception ex){

        }

        Object[] array = list.toArray();
        Arrays.sort(array);

        System.out.println("长度:"+array.length);
        for(Object v:array){

            System.out.println((Long)v);

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
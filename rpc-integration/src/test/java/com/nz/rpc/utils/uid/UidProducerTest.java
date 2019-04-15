package com.nz.rpc.utils.uid;

import org.junit.Test;

import static org.junit.Assert.*;



public class UidProducerTest {

    @Test
    public void UUIDtest(){

        UidProducer uidProducer = new UUidProducer();

        long start = 0;
        long end = 0;

        int i = 0 ;
        long count = 10;
        start = System.currentTimeMillis();

       /* while ( i++ < count)
        {
            uidProducer.getUid();
        }
        end = System.currentTimeMillis();
        System.out.println(start);
        System.out.println( uidProducer.getUid());
        System.out.println("总时间=" + (end-start));*/


    }
}
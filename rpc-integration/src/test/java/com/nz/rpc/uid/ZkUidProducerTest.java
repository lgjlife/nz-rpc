package com.nz.rpc.uid;


import org.junit.Test;

public class ZkUidProducerTest {

    UidProducer uidProducer =  new ZkUidProducer(ZkClientFatory.zkCli());

    @Test
    public void getUid() {

        long uid1 = uidProducer.getUid();
        long uid2 = uidProducer.getUid();
        long uid3 = uidProducer.getUid();

        System.out.println( uid1 + "  " + uid2 + "  "+uid3);
    }

    /**
     *功能描述
     * @author lgj
     * @Description
     *
     * 1000 ----> 13086ms
     * 10000  --->121165

     *
     * @date 4/23/19
     * @param:
     * @return:
     *
    */
    @Test
    public  void time(){


        int count = 0;
        int max = 10000;
        long start = System.currentTimeMillis();

        while (count++<max){
            long uid = uidProducer.getUid();
        }

        long end = System.currentTimeMillis();

        System.out.println(count + "--->" + (end-start));

    }


}
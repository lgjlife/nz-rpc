package com.nz.rpc.uid;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 *功能描述
 * @author lgj
 * @Description 雪花算法实现uid
 * @date 4/23/19
*/
@Slf4j
public class CustomProducer implements UidProducer {

    //序列号12 + 机器号10 + 时间戳(ms)41 = 63
    private final int ENQUENCE_BITS_LEN = 12;
    private final int MACID_BITS_LEN = 10;
    private final int TIMESTAMP_BITS_LEN = 41;

    private final int ENQUENCE_MASK = getMask(ENQUENCE_BITS_LEN);
    private final int MACID_MASK = getMask(MACID_BITS_LEN);
    private final int TIMESTAMP_MASK = getMask(TIMESTAMP_BITS_LEN);


    private final int ENQUENCE_BITS_OFFSET = 0;
    private final int MACID_BITS_OFFSET = 12;
    private final int TIMESTAMP_BITS_OFFSET = 22;





    private  long timestamp;
    private AtomicInteger equence = new AtomicInteger(0);
    private ReentrantLock lock = new ReentrantLock();
    private  long macId;

    public CustomProducer(long macId) {
        this.macId = macId;
    }




    @Override
    public   long getUid(){


        long result = 0;

        try{
            lock.lock();
            long  curTimeStamp = System.currentTimeMillis();
            //不同的ms,序列号清零
            if(curTimeStamp != timestamp){
                equence.set(0);
                timestamp=curTimeStamp;
            }
            //相同的ms
            else {

                equence.incrementAndGet();
            }
            result =   ((timestamp & TIMESTAMP_MASK)<<TIMESTAMP_BITS_OFFSET)
                    |((macId & MACID_MASK)<<MACID_BITS_OFFSET)
                    |((equence.get() & ENQUENCE_MASK) <<ENQUENCE_BITS_OFFSET);
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            lock.unlock();
        }
        return result;
    }

    /**
     *功能描述
     * @author lgj
     * @Description
     *    1---> 0b00000001
     *    2---> 0b00000011
     *    3---> 0b00000111
     *    4---> 0b00001111
     *
     * @date 4/23/19
     * @param:
     * @return:
     *
    */
    private static int getMask(int numBit){
        int result = 0;

        for(int i = 0; i< numBit; i++){
            result |= 1 << i;

        }

        return  result;

    }


    public static void main(String args[]){

        for(int i = 0; i< 8; i++){
            System.out.println(getMask(i)+"");
        }

    }

}

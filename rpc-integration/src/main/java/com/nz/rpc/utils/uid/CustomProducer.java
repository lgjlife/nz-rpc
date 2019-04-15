package com.nz.rpc.utils.uid;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
public class CustomProducer implements UidProducer {

    private  long timestamp;
    private AtomicInteger equence = new AtomicInteger(0);
    private ReentrantLock lock = new ReentrantLock();


    @Override
    public String getUidForString() {

        throw  new UnsupportedOperationException();
    }

    @Override
    public long getUidForLong() {
        return  getUid();
    }


    private  long getUid(){
        int e = 0;
        long result = 0;

        try{
            lock.lock();
            long  curTimeStamp = System.currentTimeMillis();
            if(curTimeStamp != timestamp){

                equence.set(0);
                timestamp=curTimeStamp;
                result = (timestamp<<5)|(equence.get() & 0x1F);

            }
            else {

                result =   (timestamp<<5)|(equence.incrementAndGet() & 0x1F);
            }
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        finally{
            lock.unlock();
        }

        return result;
    }


}

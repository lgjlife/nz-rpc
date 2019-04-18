package com.nz.rpc.serialization;

public class TimeUtil {

    private  long startTime;
    private  long endTime;
    private  long timeSum;
    private  long count;

    public  void init(){
        timeSum = 0;
        count = 0;
    }

    public    void start(){
        startTime = System.nanoTime();

    }

    public  void end(){
        endTime = System.nanoTime();
        timeSum += (endTime-startTime);
        count++;
    }

    public   long getAvrTimeNs(){
        return (timeSum/count);
    }
    public   long getAvrTimeUs(){
        return (timeSum/count)/1000;
    }

    public   long getAvrTimeMs(){
        return (timeSum/count)/1000000;
    }

}

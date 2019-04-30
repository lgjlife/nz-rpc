package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.interceptor.exception.RequestTimeOutException;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class TimeOutInterceptor implements Interceptor {




    AtomicLong avr = new AtomicLong(0);
    AtomicLong nums = new AtomicLong(0);

    ReentrantLock lock = new ReentrantLock();

    private static final long TIME_OUT = 20000000;
    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {
        long start = System.nanoTime()/1000;
        Object result = invocation.executeNext();

        long end = System.nanoTime()/1000;
        long delta = end -start;
        if(result != null){
            try{
                lock.lock();
                log.info("function[{}]  last-avr =[{}],delta=[{}],nums=[{}]",invocation.getMethod().getName(),
                        avr.get(),delta,nums.get());
                System.out.println(nums.get()+ "  " + delta + "  " + avr.get());
                float avrNew = MathUtil.calcAverage(avr.get(),delta,nums.addAndGet(1));
                avr.set((long)(avrNew));
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }
            finally{
                lock.unlock();
            }
        }
        else {
            System.out.println("result == null-----------------------------------");
        }

        if(TIME_OUT < (delta)){
            String message  = new StringBuilder().append("request [")
                    .append(invocation.getMethod().getName())
                    .append("] request timeout ! max = ")
                    .toString();
            throw new RequestTimeOutException(message);
        }
        return result;
    }





}

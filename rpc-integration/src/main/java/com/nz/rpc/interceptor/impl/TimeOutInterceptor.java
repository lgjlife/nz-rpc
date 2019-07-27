package com.nz.rpc.interceptor.impl;

import com.nz.rpc.interceptor.Interceptor;
import com.nz.rpc.invocation.client.ClientInvocation;
import com.nz.rpc.uid.UidProducer;
import com.nz.rpc.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 *功能描述
 * @author lgj
 * @Description 超时处理
 * @date 5/6/19
*/
@Slf4j
public class TimeOutInterceptor implements Interceptor {

    private UidProducer uidProducer ;
    private Map<String,RequestAvrTime> avrTimeMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(ClientInvocation invocation) throws Exception {

        long start = System.nanoTime()/1000;
        Object result = invocation.executeNext();
        long end = System.nanoTime()/1000;

        if(result != null){
            Method method = invocation.getMethod();
            String methodName = method.getDeclaringClass().getName()+"."+method.getName();
            RequestAvrTime requestAvrTime =  avrTimeMap.get(methodName);
            //双检锁，避免并发问题
            if(requestAvrTime == null){
                synchronized (avrTimeMap){
                    if(avrTimeMap.get(methodName) == null){
                        requestAvrTime = new RequestAvrTime(methodName);
                        avrTimeMap.put(methodName,requestAvrTime);
                    }
                }

            }
            requestAvrTime.record(start,end);
            requestAvrTime.print();
        }
        else {
           log.error("result = null-----------------------------------");
        }
        return result;
    }

    /**
     *功能描述 
     * @author lgj
     * @Description 记录时间操作类
     * @date 7/13/19
    */
    class RequestAvrTime{
        //请求方法名称 接口+方法名
        private String method;
        //平均时间
        private AtomicLong avr = new AtomicLong(0);
        //请求次数
        private AtomicLong nums = new AtomicLong(0);
        //最近一段时间的最大值
        private AtomicLong max = new AtomicLong(0);


        private ReentrantLock lock = new ReentrantLock();

        public RequestAvrTime(String method) {
            this.method = method;
        }

        /**
         *功能描述
         * @author lgj
         * @Description  记录平均时间，注意并发问题
         * @date 7/13/19
         * @param:
         * @return:
         *
        */
        public void record(long startTime, long endTime){
            try{
                lock.lock();
                reset();
                long delta = endTime -startTime;
                float avrNew = MathUtil.calcAverage(avr.get(),delta,nums.addAndGet(1));
                avr.set((long)(avrNew));
                if(avr.get() > max.get()){
                    max.set(avr.get());
                }
            }
            catch(Exception ex){
                log.error(ex.getMessage());
            }
            finally{
                lock.unlock();
            }

        }

        public void print(){
            if(log.isDebugEnabled()){
                log.debug("THis Request method[{}] avr time is [{}]ms,Max time is [{}]",method,avr.get()/1000,max.get()/1000);
            }

        }

        /**
         *功能描述 
         * @author lgj
         * @Description  参数恢复初始化，避免突变值对平均值的影响
         * @date 7/13/19
         * @param: 
         * @return:  
         *
        */
        private void reset(){
            if(nums.incrementAndGet()>50){
                avr.set(0);
                nums.set(0);
                max.set(0);
            }

        }

    }



}

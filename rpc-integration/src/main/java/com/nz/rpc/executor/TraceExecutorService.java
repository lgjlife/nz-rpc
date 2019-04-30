package com.nz.rpc.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 *功能描述
 * @author lgj
 * @Description 重写线程池,支持打印线程池异常错误堆栈
 * @date 4/30/19
*/
@Slf4j
public class TraceExecutorService extends ThreadPoolExecutor {

    public TraceExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public TraceExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public TraceExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public TraceExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task,TraceException()));
    }

    @Override
    public void execute(Runnable command) {
        super.execute(wrap(command,TraceException()));
    }

    private  Exception TraceException(){
        return new Exception("TraceExecutorService exception:");
    }


    private Runnable wrap(Runnable task,Exception exception){

        return new Runnable() {
            @Override
            public void run() {

                try{
                    task.run();
                }
                catch(Exception ex){

                    log.error(exception.getMessage()  +":" + exception.getCause() );
                    log.error(ex.getMessage()  +":" + ex.getCause() );
                    ex.printStackTrace();
                   throw  ex;
                }
            }
        };
    }
}

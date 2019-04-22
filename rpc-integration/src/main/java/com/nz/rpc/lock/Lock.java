package com.nz.rpc.lock;

public interface Lock {



    boolean tryLock(String lockKey,int millisecondsToExpire,int timeoutMs) throws  Exception;
    void unlock(String lockKey);

}

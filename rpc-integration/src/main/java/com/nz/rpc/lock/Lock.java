package com.nz.rpc.lock;

public interface Lock {


    boolean lock(String lockKey,int millisecondsToExpire);
    boolean tryLock(String lockKey,int millisecondsToExpire,int timeoutMs);
    void unlock(String lockKey);

}

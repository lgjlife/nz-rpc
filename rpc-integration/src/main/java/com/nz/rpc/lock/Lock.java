package com.nz.rpc.lock;

public interface Lock {


    boolean lock(String lockStr,int millisecondsToExpire);
    boolean tryLock(String lockStr,int millisecondsToExpire,int timeoutMs);
    void unlock(String lockStr);

}

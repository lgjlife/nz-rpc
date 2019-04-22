package com.nz.rpc.lock.exception;

public class RequestLockException extends Exception {

    public RequestLockException(String message) {
        super(message);
    }

    public RequestLockException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.nz.rpc.interceptor.exception;

public class RequestTimeOutException extends  Exception {

    public RequestTimeOutException(String message) {
        super(message);
    }

    public RequestTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }
}

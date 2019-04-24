package com.nz.rpc.interceptor.exception;

public class AddInterceptorException extends Exception{

    public AddInterceptorException(String message) {
        super(message);
    }

    public AddInterceptorException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.nz.rpc.netty.exception;

public class RpcResponseException extends Exception {

    public RpcResponseException() {
    }

    public RpcResponseException(String message) {
        super(message);
    }

    public RpcResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}

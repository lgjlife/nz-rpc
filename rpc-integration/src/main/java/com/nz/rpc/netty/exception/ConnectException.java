package com.nz.rpc.netty.exception;

public class ConnectException extends Exception {

    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}

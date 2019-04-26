package com.nz.rpc.loadbalance.exception;

public class LoadbalanceException extends  Exception {

    public LoadbalanceException() {
    }

    public LoadbalanceException(String message) {
        super(message);
    }

    public LoadbalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

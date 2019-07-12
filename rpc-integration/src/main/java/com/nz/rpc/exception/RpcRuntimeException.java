package com.nz.rpc.exception;

public class RpcRuntimeException extends  RuntimeException {

    public RpcRuntimeException() {
        super();
    }

    public RpcRuntimeException(String message) {
        super(message);
    }

    public RpcRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

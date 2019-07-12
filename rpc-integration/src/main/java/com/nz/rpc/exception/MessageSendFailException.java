package com.nz.rpc.exception;


/**
 *功能描述 
 * @author lgj
 * @Description 消息发送异常
 * @date 7/12/19
*/
public class MessageSendFailException extends Exception {

    public MessageSendFailException() {
    }

    public MessageSendFailException(String message) {
        super(message);
    }

    public MessageSendFailException(String message, Throwable cause) {
        super(message, cause);
    }
}

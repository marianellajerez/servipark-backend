package com.servipark.backend.exception;

public abstract class BaseException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    public BaseException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
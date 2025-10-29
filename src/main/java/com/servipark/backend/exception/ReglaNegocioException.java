package com.servipark.backend.exception;

public class ReglaNegocioException extends BaseException {
    public ReglaNegocioException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
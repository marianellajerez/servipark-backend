package com.servipark.backend.exception;

public class ConflictoDeDatosException extends BaseException {
    public ConflictoDeDatosException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
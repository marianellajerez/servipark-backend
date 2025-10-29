package com.servipark.backend.exception;

public class RecursoNoEncontradoException extends BaseException {
    public RecursoNoEncontradoException(String messageKey, Object... args) {
        super(messageKey, args);
    }
}
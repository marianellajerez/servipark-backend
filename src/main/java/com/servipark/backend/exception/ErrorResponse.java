package com.servipark.backend.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String error,
        LocalDateTime timestamp
) {
}
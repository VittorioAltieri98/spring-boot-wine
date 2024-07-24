package com.wine.ai_service.exception;

public class QuotaExceededException extends RuntimeException{

    public QuotaExceededException(String message) {
        super(message);
    }

    public QuotaExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.wine.ai_service.exception;

public class CustomJsonParseException extends RuntimeException{

    public CustomJsonParseException(String message) {
        super(message);
    }

    public CustomJsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

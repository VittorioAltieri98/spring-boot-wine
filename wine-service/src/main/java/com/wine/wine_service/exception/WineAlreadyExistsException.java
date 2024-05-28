package com.wine.wine_service.exception;

public class WineAlreadyExistsException extends RuntimeException {

    public WineAlreadyExistsException(String message) {
        super(message);
    }
}

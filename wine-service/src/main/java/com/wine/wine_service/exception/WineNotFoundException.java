package com.wine.wine_service.exception;


public class WineNotFoundException extends RuntimeException {

    public WineNotFoundException(String message) {
        super(message);
    }
}

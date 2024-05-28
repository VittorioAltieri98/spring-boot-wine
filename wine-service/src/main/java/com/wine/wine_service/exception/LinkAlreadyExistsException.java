package com.wine.wine_service.exception;

public class LinkAlreadyExistsException extends RuntimeException {

    public LinkAlreadyExistsException(String message) {
        super(message);
    }
}

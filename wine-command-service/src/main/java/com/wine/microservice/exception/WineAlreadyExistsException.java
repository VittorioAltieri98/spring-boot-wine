package com.wine.microservice.exception;

public class WineAlreadyExistsException extends RuntimeException {

    public WineAlreadyExistsException(String message) {
        super(message);
    }
}

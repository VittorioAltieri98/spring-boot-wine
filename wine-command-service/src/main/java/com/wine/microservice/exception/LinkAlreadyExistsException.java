package com.wine.microservice.exception;

public class LinkAlreadyExistsException extends RuntimeException {

    public LinkAlreadyExistsException(String message) {
        super(message);
    }
}

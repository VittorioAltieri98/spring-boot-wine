package com.wine.wine_command.exception;

public class WineAlreadyExistsException extends RuntimeException {

    public WineAlreadyExistsException(String message) {
        super(message);
    }
}

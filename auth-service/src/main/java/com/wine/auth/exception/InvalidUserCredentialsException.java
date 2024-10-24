package com.wine.auth.exception;

public class InvalidUserCredentialsException extends RuntimeException{

    public InvalidUserCredentialsException(String message) {
        super(message);
    }
}

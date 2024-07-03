package com.wine.ai_service.exception;



import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WinePairingNotFoundException.class)
    public Map<String, String> handleWinePairingNotFoundException(WinePairingNotFoundException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserWinePairingAlreadyExistsException.class)
    public Map<String, String> handleUserWinePairingAlreadyExistsException(UserWinePairingAlreadyExistsException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }
}

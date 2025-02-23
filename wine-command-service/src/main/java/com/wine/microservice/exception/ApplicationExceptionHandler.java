package com.wine.microservice.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex){

        Map<String, String> errorMap = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors){
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WineNotFoundException.class)
    public Map<String, String> handleWineNotFoundException(WineNotFoundException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }


    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(LinkAlreadyExistsException.class)
    public Map<String, String> handleLinkAlreadyExistsException(LinkAlreadyExistsException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(WineAlreadyExistsException.class)
    public Map<String, String> handleWineAlreadyExistsException(WineAlreadyExistsException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

}

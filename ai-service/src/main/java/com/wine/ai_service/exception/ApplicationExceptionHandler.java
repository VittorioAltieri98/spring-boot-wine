package com.wine.ai_service.exception;



import io.grpc.StatusRuntimeException;
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserWinePairingNotFoundException.class)
    public Map<String, String> handleUserWinePairingNotFoundException(UserWinePairingNotFoundException ex){
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(QuotaExceededException.class)
    public Map<String, String> handleQuotaExceededException(QuotaExceededException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomJsonParseException.class)
    public Map<String, String> handleCustomJsonParseException(CustomJsonParseException ex){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Scegli la status code appropriata
    @ExceptionHandler(StatusRuntimeException.class)
    public Map<String, String> handleStatusRuntimeException(StatusRuntimeException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("Messaggio di errore", "Quota limite superata, riprovare tra qualche secondo");
        return errorMap;
    }

}

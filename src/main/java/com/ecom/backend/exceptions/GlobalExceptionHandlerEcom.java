package com.ecom.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerEcom {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> resourceArgumentNotValidException(
            MethodArgumentNotValidException e
    )
    {
        Map<String,String> map = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach( err -> {
                    String fieldName = err.getObjectName();
                    String error = err.getDefaultMessage();
                    map.put(fieldName,error);
                });

        return ResponseEntity.status(e.getStatusCode()).body(map);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundException(ResourceNotFoundException e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

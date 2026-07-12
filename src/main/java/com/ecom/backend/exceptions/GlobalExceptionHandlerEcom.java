package com.ecom.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
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

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String,String>> sqlDuplicateException(
            SQLIntegrityConstraintViolationException e
    )
    {
        Map<String,String> map = new HashMap<>();
        map.put("message","Resource already exists.");
        map.put("error",e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFound(ResourceNotFoundException e)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> resourceAlreadyExists(ResourceAlreadyExistsException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(GenericAPIException.class)
    public ResponseEntity<String> genericException(GenericAPIException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<String> nullUserDetail(InternalAuthenticationServiceException e)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

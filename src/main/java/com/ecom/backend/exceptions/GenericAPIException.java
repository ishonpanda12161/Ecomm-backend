package com.ecom.backend.exceptions;

public class GenericAPIException extends RuntimeException {
    String message;

    public GenericAPIException(String message) {
        super(message);
        this.message = message;
    }
}

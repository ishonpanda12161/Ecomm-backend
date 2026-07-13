package com.ecom.backend.exceptions;

public class JwtException extends RuntimeException {

    String errorMessage;
    String errorType;

    public JwtException(String errorMessage,String errorType) {
        super("JWT Error - "+"Type: "+errorType+". Message: "+errorMessage);
        this.errorMessage = errorMessage;
        this.errorType = errorType;
    }

}

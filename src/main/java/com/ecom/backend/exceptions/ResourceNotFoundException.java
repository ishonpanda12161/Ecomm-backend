package com.ecom.backend.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    String resourceName;
    String fieldName;

    String field;
    Long id;

    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(resourceName+" not found with "+fieldName+": "+field+".");
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Long id) {
        super(resourceName+" not found with "+fieldName+": "+id+".");
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.id = id;
    }
}

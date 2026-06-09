package com.ecom.backend.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

    String resourceName;
    String fieldName;

    String field;
    Long id;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, String field) {
        super(resourceName+" already exists with "+fieldName+": "+field+".");
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceAlreadyExistsException(String resourceName, Long id) {
        super(resourceName+" already exists with id: "+id+".");
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.id = id;
    }
}

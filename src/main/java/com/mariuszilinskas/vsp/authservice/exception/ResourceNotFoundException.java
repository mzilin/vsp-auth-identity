package com.mariuszilinskas.vsp.authservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class<?> entity, String type, Object value) {
        super(entity.getSimpleName() + " with " + type + " '" + value + "' doesn't exist");
    }
}

package com.example.taskmanager.common.exception;

/**
 * Thrown when a requested resource does not exist (mapped to HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object id) {
        return new ResourceNotFoundException("%s not found with id: %s".formatted(resource, id));
    }
}

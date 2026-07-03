package com.example.taskmanager.common.exception;

/**
 * Thrown when creating a resource that violates a uniqueness constraint (mapped to HTTP 409).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

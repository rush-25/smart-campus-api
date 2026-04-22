package com.smartcampus.exception;

/**
 * Thrown when a requested resource cannot be found.
 * Mapped to HTTP 404 Not Found by ResourceNotFoundExceptionMapper.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

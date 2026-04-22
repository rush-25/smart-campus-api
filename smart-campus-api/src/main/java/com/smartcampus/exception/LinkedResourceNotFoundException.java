package com.smartcampus.exception;

/**
 * Thrown when a request body references a linked resource that does not exist.
 * Example: creating a sensor with a roomId that has no matching room.
 *
 * Mapped to HTTP 422 Unprocessable Entity.
 *
 * Why 422 and not 404?
 *   404 → the request URI doesn't exist.
 *   422 → the URI is valid, the JSON is syntactically correct, but the
 *          content is semantically invalid (the linked entity is missing).
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}

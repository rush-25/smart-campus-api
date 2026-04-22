package com.smartcampus.exception;

/** Thrown when the request body fails basic field validation. */
public class ValidationException extends RuntimeException {
    private final String field;

    public ValidationException(String message) {
        super(message);
        this.field = null;
    }

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}

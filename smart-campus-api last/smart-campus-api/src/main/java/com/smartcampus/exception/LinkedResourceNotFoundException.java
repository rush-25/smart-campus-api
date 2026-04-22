package com.smartcampus.exception;

/** Thrown when a sensor references a roomId that does not exist. */
public class LinkedResourceNotFoundException extends RuntimeException {
    private final String field;
    private final String value;

    public LinkedResourceNotFoundException(String field, String value) {
        super("Linked resource not found: field='" + field + "', value='" + value + "' does not reference an existing resource.");
        this.field = field;
        this.value = value;
    }

    public String getField() { return field; }
    public String getValue() { return value; }
}

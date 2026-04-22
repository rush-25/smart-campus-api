package com.smartcampus.exception;

/** Thrown when a specific resource (room/sensor/reading) cannot be found by ID. */
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(resourceType + " with ID '" + resourceId + "' was not found.");
        this.resourceType = resourceType;
        this.resourceId   = resourceId;
    }

    public String getResourceType() { return resourceType; }
    public String getResourceId()   { return resourceId; }
}

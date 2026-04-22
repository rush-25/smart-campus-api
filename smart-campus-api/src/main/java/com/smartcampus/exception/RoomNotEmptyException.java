package com.smartcampus.exception;

/**
 * Thrown when attempting to delete a room that still has sensors installed.
 *
 * Mapped to HTTP 409 Conflict — the request is well-formed, but it
 * conflicts with current resource state (sensors are still present).
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}

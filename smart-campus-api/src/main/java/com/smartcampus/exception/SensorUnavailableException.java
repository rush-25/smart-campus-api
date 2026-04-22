package com.smartcampus.exception;

/**
 * Thrown when a client attempts to post a reading to an inactive sensor.
 *
 * Mapped to HTTP 403 Forbidden — the sensor exists but write access is denied
 * because its operational state does not permit new readings.
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}

package com.smartcampus.model;

import java.time.Instant;

/**
 * Represents a single data reading captured by a sensor.
 *
 * Readings are sub-resources of sensors:
 *   POST /api/v1/sensors/{id}/readings
 *   GET  /api/v1/sensors/{id}/readings
 *
 * The timestamp is set automatically to the current UTC instant
 * when no timestamp is provided in the request body.
 */
public class SensorReading {

    private int    id;
    private int    sensorId;
    private double value;
    private String timestamp;   // ISO-8601 UTC string, e.g. "2025-03-15T10:30:00Z"

    /** Required by Jackson for JSON deserialisation. */
    public SensorReading() {}

    public SensorReading(int id, int sensorId, double value) {
        this.id        = id;
        this.sensorId  = sensorId;
        this.value     = value;
        this.timestamp = Instant.now().toString();
    }

    // ── Getters & Setters ──────────────────────────────────────────

    public int  getId()          { return id; }
    public void setId(int id)    { this.id = id; }

    public int  getSensorId()              { return sensorId; }
    public void setSensorId(int sensorId)  { this.sensorId = sensorId; }

    public double getValue()               { return value; }
    public void   setValue(double value)   { this.value = value; }

    public String getTimestamp()                   { return timestamp; }
    public void   setTimestamp(String timestamp)   { this.timestamp = timestamp; }
}

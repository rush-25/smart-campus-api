package com.smartcampus.model;

import java.util.UUID;

/**
 * SensorReading entity representing a historical measurement event from a sensor.
 */
public class SensorReading {

    private String id;        // Unique reading event ID (UUID recommended)
    private long timestamp;   // Epoch time (ms) when the reading was captured
    private double value;     // The actual metric value recorded by the hardware
    private String sensorId;  // Extra: back-reference to the parent sensor
    private String note;      // Extra: optional annotation for the reading

    public SensorReading() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public SensorReading(double value, String sensorId) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.value = value;
        this.sensorId = sensorId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "SensorReading{id='" + id + "', sensorId='" + sensorId + "', value=" + value + ", timestamp=" + timestamp + "}";
    }
}

package com.smartcampus.model;

/**
 * Sensor entity representing a hardware sensor deployed in a campus room.
 * Valid statuses: ACTIVE, MAINTENANCE, OFFLINE
 */
public class Sensor {

    public static final String STATUS_ACTIVE      = "ACTIVE";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String STATUS_OFFLINE     = "OFFLINE";

    private String id;            // Unique identifier, e.g., "TEMP-001"
    private String type;          // Category: "Temperature", "Occupancy", "CO2", "Humidity", etc.
    private String status;        // Current state: ACTIVE, MAINTENANCE, or OFFLINE
    private double currentValue;  // Most recent measurement
    private String roomId;        // Foreign key linking to the Room
    private String unit;          // Extra: measurement unit, e.g., "°C", "ppm", "%"
    private String description;   // Extra: human-readable description

    public Sensor() {}

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(this.status);
    }

    public boolean isInMaintenance() {
        return STATUS_MAINTENANCE.equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "Sensor{id='" + id + "', type='" + type + "', status='" + status + "', roomId='" + roomId + "'}";
    }
}

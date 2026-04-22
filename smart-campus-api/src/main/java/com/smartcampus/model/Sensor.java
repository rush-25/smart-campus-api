package com.smartcampus.model;

/**
 * Represents an IoT sensor installed in a campus room.
 *
 * Each sensor belongs to exactly one room (via roomId).
 * The 'active' flag controls whether new readings can be submitted.
 * 'currentValue' is automatically updated each time a new reading is added.
 */
public class Sensor {

    private int    id;
    private String type;          // e.g. "CO2", "Temperature", "Humidity"
    private String unit;          // e.g. "ppm", "Celsius", "%"
    private int    roomId;
    private double currentValue;  // Updated automatically on each new reading
    private boolean active;       // false → SensorUnavailableException (403)

    /** Required by Jackson for JSON deserialisation. */
    public Sensor() {}

    public Sensor(int id, String type, String unit, int roomId) {
        this.id           = id;
        this.type         = type;
        this.unit         = unit;
        this.roomId       = roomId;
        this.currentValue = 0.0;
        this.active       = true;
    }

    // ── Getters & Setters ──────────────────────────────────────────

    public int  getId()            { return id; }
    public void setId(int id)      { this.id = id; }

    public String getType()              { return type; }
    public void   setType(String type)   { this.type = type; }

    public String getUnit()              { return unit; }
    public void   setUnit(String unit)   { this.unit = unit; }

    public int  getRoomId()              { return roomId; }
    public void setRoomId(int roomId)    { this.roomId = roomId; }

    public double getCurrentValue()                    { return currentValue; }
    public void   setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public boolean isActive()                { return active; }
    public void    setActive(boolean active) { this.active = active; }
}

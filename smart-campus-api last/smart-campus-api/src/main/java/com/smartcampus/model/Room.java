package com.smartcampus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Room entity representing a physical room on campus.
 */
public class Room {

    private String id;       // Unique identifier, e.g., "LIB-301"
    private String name;     // Human-readable name, e.g., "Library Quiet Study"
    private int capacity;    // Maximum occupancy for safety regulations
    private String building; // Extra: Building name for better organisation
    private String floor;    // Extra: Floor number/name
    private List<String> sensorIds = new ArrayList<>(); // Sensor IDs deployed in this room

    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Room(String id, String name, int capacity, String building, String floor) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.building = building;
        this.floor = floor;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }

    public void addSensorId(String sensorId) {
        if (!this.sensorIds.contains(sensorId)) {
            this.sensorIds.add(sensorId);
        }
    }

    public void removeSensorId(String sensorId) {
        this.sensorIds.remove(sensorId);
    }

    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "', capacity=" + capacity + "}";
    }
}

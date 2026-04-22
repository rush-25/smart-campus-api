package com.smartcampus.model;

/**
 * Represents a physical room on campus.
 *
 * Rooms are the top-level parent resource.
 * Each sensor must be associated with exactly one room.
 */
public class Room {

    private int    id;
    private String name;
    private String location;
    private int    capacity;

    /** Required by Jackson for JSON deserialisation. */
    public Room() {}

    public Room(int id, String name, String location, int capacity) {
        this.id       = id;
        this.name     = name;
        this.location = location;
        this.capacity = capacity;
    }

    // ── Getters & Setters ──────────────────────────────────────────

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getName()                  { return name; }
    public void   setName(String name)       { this.name = name; }

    public String getLocation()                    { return location; }
    public void   setLocation(String location)     { this.location = location; }

    public int  getCapacity()                { return capacity; }
    public void setCapacity(int capacity)    { this.capacity = capacity; }
}

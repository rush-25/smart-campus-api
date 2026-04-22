package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton in-memory data store.
 *
 * Acts as the single source of truth for all API data during the
 * application's lifetime. Uses AtomicInteger for thread-safe ID generation.
 *
 * In a production system this layer would be replaced by JPA / a database;
 * the service layer above it would remain structurally unchanged.
 */
public class DataStore {

    // ── Singleton ──────────────────────────────────────────────────
    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() { return INSTANCE; }

    private DataStore() { seedData(); }

    // ── Storage Maps ───────────────────────────────────────────────
    private final Map<Integer, Room>          rooms    = new HashMap<>();
    private final Map<Integer, Sensor>        sensors  = new HashMap<>();
    private final Map<Integer, SensorReading> readings = new HashMap<>();

    // ── Auto-incrementing ID counters ──────────────────────────────
    private final AtomicInteger roomIdCounter    = new AtomicInteger(1);
    private final AtomicInteger sensorIdCounter  = new AtomicInteger(1);
    private final AtomicInteger readingIdCounter = new AtomicInteger(1);

    // ── Public Accessors ───────────────────────────────────────────

    public Map<Integer, Room>          getRooms()    { return rooms; }
    public Map<Integer, Sensor>        getSensors()  { return sensors; }
    public Map<Integer, SensorReading> getReadings() { return readings; }

    public int nextRoomId()    { return roomIdCounter.getAndIncrement(); }
    public int nextSensorId()  { return sensorIdCounter.getAndIncrement(); }
    public int nextReadingId() { return readingIdCounter.getAndIncrement(); }

    // ── Seed Data ──────────────────────────────────────────────────

    /**
     * Populates the store with realistic sample data on startup so the API
     * is immediately explorable without needing to POST data first.
     */
    private void seedData() {

        // ── Rooms ──
        Room r1 = new Room(nextRoomId(), "Lab A101",          "Building A, Floor 1", 30);
        Room r2 = new Room(nextRoomId(), "Lecture Hall B201", "Building B, Floor 2", 200);
        Room r3 = new Room(nextRoomId(), "Server Room C001",  "Building C, Basement", 5);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        // ── Sensors ──
        Sensor s1 = new Sensor(nextSensorId(), "CO2",         "ppm",     r1.getId());
        Sensor s2 = new Sensor(nextSensorId(), "Temperature", "Celsius", r1.getId());
        Sensor s3 = new Sensor(nextSensorId(), "Humidity",    "%",       r2.getId());
        Sensor s4 = new Sensor(nextSensorId(), "CO2",         "ppm",     r2.getId());
        // s4 is marked inactive to demonstrate SensorUnavailableException
        s4.setActive(false);
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
        sensors.put(s4.getId(), s4);

        // ── Readings ──
        SensorReading rd1 = new SensorReading(nextReadingId(), s1.getId(), 412.5);
        SensorReading rd2 = new SensorReading(nextReadingId(), s1.getId(), 450.0);
        SensorReading rd3 = new SensorReading(nextReadingId(), s2.getId(), 22.3);
        SensorReading rd4 = new SensorReading(nextReadingId(), s3.getId(), 65.0);
        readings.put(rd1.getId(), rd1);
        readings.put(rd2.getId(), rd2);
        readings.put(rd3.getId(), rd3);
        readings.put(rd4.getId(), rd4);

        // Keep sensor currentValues consistent with seed readings
        s1.setCurrentValue(450.0);
        s2.setCurrentValue(22.3);
        s3.setCurrentValue(65.0);
    }
}

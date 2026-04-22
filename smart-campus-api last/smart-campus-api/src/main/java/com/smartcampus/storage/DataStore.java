package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe singleton in-memory data store.
 *
 * Uses ConcurrentHashMap to handle concurrent requests safely without
 * explicit synchronisation on read operations. Write operations that
 * span multiple maps are handled in resource methods.
 *
 * Pre-populated with sample data for demonstration purposes.
 */
public class DataStore {

    // ── Singleton ────────────────────────────────────────────────────────────
    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() {
        return INSTANCE;
    }

    // ── Storage maps (thread-safe) ────────────────────────────────────────────
    private final Map<String, Room>          rooms    = new ConcurrentHashMap<>();
    private final Map<String, Sensor>        sensors  = new ConcurrentHashMap<>();
    // key: sensorId → list of readings
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // ── Request metrics (for stats endpoint) ─────────────────────────────────
    private volatile long totalRequests = 0;
    private volatile long startTimeMs   = System.currentTimeMillis();

    // ── Private constructor — pre-load sample data ────────────────────────────
    private DataStore() {
        seedData();
    }

    private void seedData() {
        // Rooms
        Room r1 = new Room("LIB-301", "Library Quiet Study",  60, "Library Building",  "3rd Floor");
        Room r2 = new Room("ENG-101", "Engineering Lab A",    30, "Engineering Block",  "1st Floor");
        Room r3 = new Room("CS-205",  "Computer Science Lab", 40, "Computer Science",   "2nd Floor");
        Room r4 = new Room("CONF-01", "Main Conference Room", 20, "Administration",     "Ground Floor");

        // Sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE",      22.5, "LIB-301");
        s1.setUnit("°C"); s1.setDescription("Main temperature sensor in library");

        Sensor s2 = new Sensor("CO2-001",  "CO2",         "ACTIVE",     412.0, "LIB-301");
        s2.setUnit("ppm"); s2.setDescription("CO2 level monitor near entrance");

        Sensor s3 = new Sensor("OCC-001",  "Occupancy",   "ACTIVE",      35.0, "ENG-101");
        s3.setUnit("people"); s3.setDescription("Occupancy counter at door");

        Sensor s4 = new Sensor("TEMP-002", "Temperature", "MAINTENANCE", 19.1, "CS-205");
        s4.setUnit("°C"); s4.setDescription("Server room temperature sensor");

        Sensor s5 = new Sensor("HUM-001",  "Humidity",    "ACTIVE",      55.3, "CONF-01");
        s5.setUnit("%RH"); s5.setDescription("Humidity sensor for comfort monitoring");

        Sensor s6 = new Sensor("LIGHT-001","Lighting",    "OFFLINE",      0.0, "ENG-101");
        s6.setUnit("lux"); s6.setDescription("Smart lighting controller");

        // Link sensors to rooms
        r1.addSensorId("TEMP-001"); r1.addSensorId("CO2-001");
        r2.addSensorId("OCC-001");  r2.addSensorId("LIGHT-001");
        r3.addSensorId("TEMP-002");
        r4.addSensorId("HUM-001");

        // Save rooms
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);
        rooms.put(r4.getId(), r4);

        // Save sensors
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);
        sensors.put(s4.getId(), s4);
        sensors.put(s5.getId(), s5);
        sensors.put(s6.getId(), s6);

        // Pre-populate readings for demonstration
        addReading("TEMP-001", new SensorReading(21.0, "TEMP-001"));
        addReading("TEMP-001", new SensorReading(22.5, "TEMP-001"));
        addReading("CO2-001",  new SensorReading(400.0, "CO2-001"));
        addReading("CO2-001",  new SensorReading(412.0, "CO2-001"));
    }

    // ── Room operations ───────────────────────────────────────────────────────
    public Map<String, Room> getRooms()                { return rooms; }
    public Room getRoom(String id)                     { return rooms.get(id); }
    public void putRoom(String id, Room room)          { rooms.put(id, room); }
    public Room removeRoom(String id)                  { return rooms.remove(id); }
    public boolean roomExists(String id)               { return rooms.containsKey(id); }

    // ── Sensor operations ─────────────────────────────────────────────────────
    public Map<String, Sensor> getSensors()            { return sensors; }
    public Sensor getSensor(String id)                 { return sensors.get(id); }
    public void putSensor(String id, Sensor sensor)    { sensors.put(id, sensor); }
    public Sensor removeSensor(String id)              { return sensors.remove(id); }
    public boolean sensorExists(String id)             { return sensors.containsKey(id); }

    // ── Reading operations ────────────────────────────────────────────────────
    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }

    public SensorReading getReading(String sensorId, String readingId) {
        return getReadings(sensorId).stream()
                .filter(r -> r.getId().equals(readingId))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteReading(String sensorId, String readingId) {
        List<SensorReading> list = readings.get(sensorId);
        if (list == null) return false;
        return list.removeIf(r -> r.getId().equals(readingId));
    }

    // ── Metrics ───────────────────────────────────────────────────────────────
    public void incrementRequests()          { totalRequests++; }
    public long getTotalRequests()           { return totalRequests; }
    public long getUptimeSeconds()           { return (System.currentTimeMillis() - startTimeMs) / 1000; }
}

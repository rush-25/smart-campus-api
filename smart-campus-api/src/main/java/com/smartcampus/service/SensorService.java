package com.smartcampus.service;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Sensor and SensorReading business logic.
 */
public class SensorService {

    private final DataStore store = DataStore.getInstance();

    // ================================================================
    //  SENSOR OPERATIONS
    // ================================================================

    /**
     * Returns all sensors, with optional filtering by type.
     *
     * @param type Sensor type filter (e.g. "CO2"). Null or blank → return all.
     */
    public List<Sensor> getAllSensors(String type) {
        List<Sensor> all = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.isBlank()) {
            return all.stream()
                      .filter(s -> s.getType().equalsIgnoreCase(type))
                      .collect(Collectors.toList());
        }
        return all;
    }

    /**
     * Fetches a single sensor by ID.
     *
     * @throws ResourceNotFoundException (→ 404) if no sensor exists with this ID.
     */
    public Sensor getSensorById(int id) {
        Sensor sensor = store.getSensors().get(id);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found with ID: " + id);
        }
        return sensor;
    }

    /**
     * Creates a new sensor.
     *
     * Validates that the referenced room exists before persisting.
     * If the room is missing we throw LinkedResourceNotFoundException (→ 422),
     * NOT a 404, because the request URI is valid — it is the request body
     * content that is semantically invalid.
     *
     * @throws LinkedResourceNotFoundException (→ 422) if roomId references a non-existent room.
     */
    public Sensor createSensor(Sensor sensor) {
        if (!store.getRooms().containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot create sensor: Room with ID " + sensor.getRoomId() + " does not exist."
            );
        }

        int newId = store.nextSensorId();
        sensor.setId(newId);
        sensor.setCurrentValue(0.0);
        sensor.setActive(true);
        store.getSensors().put(newId, sensor);
        return sensor;
    }

    // ================================================================
    //  READING OPERATIONS
    // ================================================================

    /**
     * Returns all readings recorded by a specific sensor.
     *
     * @throws ResourceNotFoundException (→ 404) if the parent sensor doesn't exist.
     */
    public List<SensorReading> getReadingsForSensor(int sensorId) {
        getSensorById(sensorId);   // validate parent exists

        return store.getReadings().values().stream()
                    .filter(r -> r.getSensorId() == sensorId)
                    .collect(Collectors.toList());
    }

    /**
     * Records a new reading for a sensor and updates the sensor's currentValue.
     *
     * @throws ResourceNotFoundException  (→ 404) if the sensor doesn't exist.
     * @throws SensorUnavailableException (→ 403) if the sensor is inactive.
     */
    public SensorReading addReading(int sensorId, SensorReading reading) {
        Sensor sensor = getSensorById(sensorId);

        if (!sensor.isActive()) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId + " is currently inactive and cannot accept readings."
            );
        }

        int newId = store.nextReadingId();
        reading.setId(newId);
        reading.setSensorId(sensorId);

        // Auto-set timestamp if the client did not provide one
        if (reading.getTimestamp() == null || reading.getTimestamp().isBlank()) {
            reading.setTimestamp(Instant.now().toString());
        }

        store.getReadings().put(newId, reading);

        // Keep the sensor's live value in sync with the latest reading
        sensor.setCurrentValue(reading.getValue());

        return reading;
    }
}

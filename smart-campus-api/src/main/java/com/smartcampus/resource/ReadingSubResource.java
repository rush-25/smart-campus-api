package com.smartcampus.resource;

import com.smartcampus.model.SensorReading;
import com.smartcampus.service.SensorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Part 4 – Sensor Reading Sub-Resource
 *
 * Handles reading operations scoped to a specific sensor.
 * Accessed via the sub-resource locator in SensorResource:
 *
 *   GET  /api/v1/sensors/{sensorId}/readings
 *   POST /api/v1/sensors/{sensorId}/readings
 *
 * This class carries NO @Path at class level and is NOT registered in
 * ApplicationConfig — it is instantiated by SensorResource's locator method.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReadingSubResource {

    private final int           sensorId;
    private final SensorService sensorService = new SensorService();

    /**
     * Receives the sensorId captured by the parent locator in SensorResource.
     */
    public ReadingSubResource(int sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /sensors/{sensorId}/readings
     *
     * Returns all readings for the given sensor.
     * Returns 200 with an empty list if the sensor has no readings yet.
     * Returns 404 if the sensor itself does not exist.
     */
    @GET
    public Response getReadings() {
        List<SensorReading> readings = sensorService.getReadingsForSensor(sensorId);
        return Response.ok(readings).build();
    }

    /**
     * POST /sensors/{sensorId}/readings
     *
     * Records a new sensor reading and automatically updates
     * the parent sensor's 'currentValue' field.
     *
     * Returns:
     *   201 Created  – the created reading + Location header
     *   403 Forbidden – sensor is inactive (SensorUnavailableExceptionMapper)
     *   404 Not Found – sensor does not exist (ResourceNotFoundExceptionMapper)
     */
    @POST
    public Response addReading(SensorReading reading) {
        SensorReading created = sensorService.addReading(sensorId, reading);
        URI location = URI.create("/api/v1/sensors/" + sensorId + "/readings/" + created.getId());
        return Response.created(location).entity(created).build();
    }
}

package com.smartcampus.resource;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.exception.ValidationException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.response.ApiResponse;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Sub-Resource for Sensor Readings — /api/v1/sensors/{sensorId}/readings
 *
 * This class is NOT annotated with @Path itself — it is instantiated and
 * returned by SensorResource's sub-resource locator method, which passes
 * the sensorId context. This is the Sub-Resource Locator Pattern.
 *
 * Benefits over defining every path in one giant controller:
 *  1. Separation of concerns — each class has one responsibility.
 *  2. Smaller, more readable files.
 *  3. Independent testability.
 *  4. Easy to extend (e.g., add /readings/stats) without touching SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private static final Logger    LOG      = Logger.getLogger(SensorReadingResource.class.getName());
    private final         DataStore store    = DataStore.getInstance();
    private final         String    sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // ── GET /api/v1/sensors/{sensorId}/readings ───────────────────────────────
    @GET
    public Response getReadings(@QueryParam("limit") Integer limit) {
        List<SensorReading> readings = store.getReadings(sensorId);

        // Optional: return only the last N readings
        if (limit != null && limit > 0 && limit < readings.size()) {
            readings = readings.subList(readings.size() - limit, readings.size());
        }

        return Response.ok(ApiResponse.ok(
                "Retrieved " + readings.size() + " reading(s) for sensor '" + sensorId + "'.",
                readings
        )).build();
    }

    // ── POST /api/v1/sensors/{sensorId}/readings ──────────────────────────────
    /**
     * Appends a new reading.
     * MAINTENANCE sensors cannot accept readings (→ 403 Forbidden).
     * On success, updates the parent Sensor's currentValue for data consistency.
     */
    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = store.getSensor(sensorId);

        // State constraint: MAINTENANCE sensors cannot accept new readings
        if (Sensor.STATUS_MAINTENANCE.equals(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }
        // OFFLINE sensors also cannot record readings
        if (Sensor.STATUS_OFFLINE.equals(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }

        if (reading == null) throw new ValidationException("Request body must not be null.");
        reading.setSensorId(sensorId);

        // Auto-set timestamp if not provided
        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        store.addReading(sensorId, reading);

        // Side effect: keep parent sensor's currentValue in sync
        sensor.setCurrentValue(reading.getValue());
        LOG.info("Reading added for sensor '" + sensorId + "': value=" + reading.getValue());

        URI location = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(location)
                .entity(ApiResponse.ok("Reading recorded successfully.", reading))
                .build();
    }

    // ── GET /api/v1/sensors/{sensorId}/readings/{readingId} ──────────────────
    @GET
    @Path("/{readingId}")
    public Response getReading(@PathParam("readingId") String readingId) {
        SensorReading reading = store.getReading(sensorId, readingId);
        if (reading == null)
            throw new ResourceNotFoundException("SensorReading", readingId);
        return Response.ok(ApiResponse.ok(reading)).build();
    }

    // ── DELETE /api/v1/sensors/{sensorId}/readings/{readingId} ───────────────
    @DELETE
    @Path("/{readingId}")
    public Response deleteReading(@PathParam("readingId") String readingId) {
        boolean deleted = store.deleteReading(sensorId, readingId);
        if (!deleted) throw new ResourceNotFoundException("SensorReading", readingId);
        LOG.info("Reading deleted: " + readingId + " from sensor: " + sensorId);
        return Response.ok(ApiResponse.ok("Reading '" + readingId + "' deleted.", null)).build();
    }

    // ── GET /api/v1/sensors/{sensorId}/readings/stats — extra feature ─────────
    @GET
    @Path("/stats")
    public Response getReadingStats() {
        List<SensorReading> readings = store.getReadings(sensorId);
        if (readings.isEmpty()) {
            return Response.ok(ApiResponse.ok("No readings available.", null)).build();
        }

        double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (SensorReading r : readings) {
            sum += r.getValue();
            if (r.getValue() < min) min = r.getValue();
            if (r.getValue() > max) max = r.getValue();
        }
        double avg = sum / readings.size();

        java.util.Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("sensorId", sensorId);
        stats.put("totalReadings", readings.size());
        stats.put("min",   min);
        stats.put("max",   max);
        stats.put("avg",   Math.round(avg * 100.0) / 100.0);
        stats.put("sum",   Math.round(sum * 100.0) / 100.0);
        stats.put("latest", readings.get(readings.size() - 1).getValue());

        return Response.ok(ApiResponse.ok("Statistics for sensor '" + sensorId + "'.", stats)).build();
    }
}

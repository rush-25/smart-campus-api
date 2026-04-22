package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.ValidationException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.response.ApiResponse;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Sensor Resource — /api/v1/sensors
 *
 * Manages sensor registration, retrieval, update, deletion, and filtered search.
 * Provides a sub-resource locator for historical readings via {sensorId}/readings.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private static final Logger   LOG   = Logger.getLogger(SensorResource.class.getName());
    private final         DataStore store = DataStore.getInstance();

    // ── GET /api/v1/sensors?type=CO2&status=ACTIVE ────────────────────────────
    /**
     * @QueryParam is preferred over path segments for filtering because:
     * - Filtering is optional — path params imply a required resource identifier.
     * - Multiple filters can be composed: ?type=CO2&status=ACTIVE
     * - Path structure should model resource hierarchy, not query criteria.
     */
    @GET
    public Response getSensors(@QueryParam("type")   String type,
                               @QueryParam("status") String status,
                               @QueryParam("roomId") String roomId) {
        List<Sensor> sensors = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.isEmpty())
            sensors.removeIf(s -> !s.getType().equalsIgnoreCase(type));

        if (status != null && !status.isEmpty())
            sensors.removeIf(s -> !s.getStatus().equalsIgnoreCase(status));

        if (roomId != null && !roomId.isEmpty())
            sensors.removeIf(s -> !roomId.equals(s.getRoomId()));

        return Response.ok(ApiResponse.ok("Retrieved " + sensors.size() + " sensor(s).", sensors)).build();
    }

    // ── POST /api/v1/sensors ──────────────────────────────────────────────────
    /**
     * @Consumes(APPLICATION_JSON): if a client sends Content-Type: text/plain,
     * JAX-RS returns HTTP 415 Unsupported Media Type before the method is even called.
     * If the body cannot be deserialised into Sensor, Jackson throws an unmarshal
     * exception which our GlobalExceptionMapper maps to HTTP 400/500.
     */
    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        validateSensor(sensor);

        // Integrity check: roomId must reference an existing room
        if (!store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("roomId", sensor.getRoomId());
        }

        if (store.sensorExists(sensor.getId())) {
            throw new ValidationException("A sensor with ID '" + sensor.getId() + "' already exists.");
        }

        // Link sensor to room
        Room room = store.getRoom(sensor.getRoomId());
        room.addSensorId(sensor.getId());

        store.putSensor(sensor.getId(), sensor);
        LOG.info("Sensor created: " + sensor.getId() + " linked to room: " + sensor.getRoomId());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location)
                .entity(ApiResponse.ok("Sensor registered successfully.", sensor))
                .build();
    }

    // ── GET /api/v1/sensors/{sensorId} ───────────────────────────────────────
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new ResourceNotFoundException("Sensor", sensorId);
        return Response.ok(ApiResponse.ok(sensor)).build();
    }

    // ── PUT /api/v1/sensors/{sensorId} ───────────────────────────────────────
    @PUT
    @Path("/{sensorId}")
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updated) {
        Sensor existing = store.getSensor(sensorId);
        if (existing == null) throw new ResourceNotFoundException("Sensor", sensorId);

        // Validate new roomId if it changed
        if (updated.getRoomId() != null && !updated.getRoomId().equals(existing.getRoomId())) {
            if (!store.roomExists(updated.getRoomId())) {
                throw new LinkedResourceNotFoundException("roomId", updated.getRoomId());
            }
            // Unlink from old room, link to new room
            Room oldRoom = store.getRoom(existing.getRoomId());
            if (oldRoom != null) oldRoom.removeSensorId(sensorId);
            Room newRoom = store.getRoom(updated.getRoomId());
            if (newRoom != null) newRoom.addSensorId(sensorId);
        }

        updated.setId(sensorId);
        store.putSensor(sensorId, updated);
        LOG.info("Sensor updated: " + sensorId);
        return Response.ok(ApiResponse.ok("Sensor updated successfully.", updated)).build();
    }

    // ── DELETE /api/v1/sensors/{sensorId} ────────────────────────────────────
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new ResourceNotFoundException("Sensor", sensorId);

        // Unlink from parent room
        Room room = store.getRoom(sensor.getRoomId());
        if (room != null) room.removeSensorId(sensorId);

        store.removeSensor(sensorId);
        LOG.info("Sensor deleted: " + sensorId);
        return Response.ok(ApiResponse.ok("Sensor '" + sensorId + "' deleted successfully.", null)).build();
    }

    // ── Sub-Resource Locator: GET|POST /api/v1/sensors/{sensorId}/readings ───
    /**
     * Sub-Resource Locator Pattern:
     * Instead of defining every nested path in this class, we delegate to a
     * dedicated SensorReadingResource. Benefits:
     *  - Separation of concerns — reading logic lives in its own class.
     *  - Easier testing — SensorReadingResource can be unit-tested in isolation.
     *  - Scalability — adding more sub-resources doesn't bloat this file.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        // Validate sensor exists before delegating
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) throw new ResourceNotFoundException("Sensor", sensorId);
        return new SensorReadingResource(sensorId);
    }

    // ── Validation helper ─────────────────────────────────────────────────────
    private void validateSensor(Sensor sensor) {
        if (sensor == null)
            throw new ValidationException("Request body must not be null.");
        if (sensor.getId() == null || sensor.getId().trim().isEmpty())
            throw new ValidationException("id", "Field 'id' is required.");
        if (sensor.getType() == null || sensor.getType().trim().isEmpty())
            throw new ValidationException("type", "Field 'type' is required.");
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty())
            throw new ValidationException("roomId", "Field 'roomId' is required.");

        String status = sensor.getStatus();
        if (status == null || status.trim().isEmpty()) {
            sensor.setStatus(Sensor.STATUS_ACTIVE); // default
        } else if (!status.equals(Sensor.STATUS_ACTIVE) &&
                   !status.equals(Sensor.STATUS_MAINTENANCE) &&
                   !status.equals(Sensor.STATUS_OFFLINE)) {
            throw new ValidationException("status",
                    "Field 'status' must be one of: ACTIVE, MAINTENANCE, OFFLINE.");
        }
    }
}

package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.service.SensorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Part 3 – Sensor Management Resource
 * Part 4 – Sensor Readings via sub-resource locator
 *
 * URI pattern: /api/v1/sensors[/{id}][/readings]
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final SensorService sensorService = new SensorService();

    /**
     * GET /sensors
     * GET /sensors?type=CO2
     *
     * Returns all sensors. The optional 'type' query parameter filters the
     * result to sensors of a specific type (case-insensitive).
     *
     * Using @QueryParam (not @PathParam) for type because it is a filter
     * on the collection, not an identifier of a specific resource.
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = sensorService.getAllSensors(type);
        return Response.ok(sensors).build();
    }

    /**
     * POST /sensors
     *
     * Creates a new sensor linked to an existing room.
     * The request body must include a valid 'roomId'.
     *
     * Returns:
     *   201 Created               – full sensor object + Location header
     *   422 Unprocessable Entity  – roomId references a room that doesn't exist
     */
    @POST
    public Response createSensor(Sensor sensor) {
        Sensor created = sensorService.createSensor(sensor);
        URI location   = URI.create("/api/v1/sensors/" + created.getId());
        return Response.created(location).entity(created).build();
    }

    /**
     * Sub-Resource Locator for /sensors/{sensorId}/readings
     *
     * This method has NO HTTP verb annotation — it is a locator, not a handler.
     * JAX-RS delegates all requests under /sensors/{sensorId}/readings to
     * ReadingSubResource, passing the captured sensorId into its constructor.
     *
     * Benefits:
     *  – Keeps SensorResource focused on sensor-level operations only
     *  – ReadingSubResource can be tested independently
     *  – URI hierarchy (/sensors/{id}/readings) clearly models ownership
     */
    @Path("/{sensorId}/readings")
    public ReadingSubResource getReadingSubResource(@PathParam("sensorId") int sensorId) {
        return new ReadingSubResource(sensorId);
    }
}

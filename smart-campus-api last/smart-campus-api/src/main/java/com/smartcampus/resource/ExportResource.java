package com.smartcampus.resource;

import com.smartcampus.storage.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Export Endpoint — GET /api/v1/export  [EXTRA / BONUS FEATURE]
 *
 * Returns a complete JSON snapshot of all campus data (rooms, sensors, readings).
 * Useful for backups, data migration, and offline analysis.
 *
 * In a production system this would be secured with admin-only authentication.
 */
@Path("/export")
@Produces(MediaType.APPLICATION_JSON)
public class ExportResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response exportAll() {
        Map<String, Object> export = new LinkedHashMap<>();
        export.put("exportedAt",  System.currentTimeMillis());
        export.put("version",     "1.0.0");
        export.put("rooms",       store.getRooms().values());
        export.put("sensors",     store.getSensors().values());

        // Include all readings per sensor
        Map<String, Object> allReadings = new LinkedHashMap<>();
        for (String sensorId : store.getSensors().keySet()) {
            allReadings.put(sensorId, store.getReadings(sensorId));
        }
        export.put("readings", allReadings);

        return Response.ok(export).build();
    }
}

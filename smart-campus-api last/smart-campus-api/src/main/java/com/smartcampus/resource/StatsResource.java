package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Statistics Endpoint — GET /api/v1/stats  [EXTRA / BONUS FEATURE]
 *
 * Provides aggregated analytics about the campus data — useful for dashboards
 * and operations teams to get a quick overview without querying every resource.
 */
@Path("/stats")
@Produces(MediaType.APPLICATION_JSON)
public class StatsResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getStats() {
        long active  = store.getSensors().values().stream()
                            .filter(s -> Sensor.STATUS_ACTIVE.equals(s.getStatus())).count();
        long maint   = store.getSensors().values().stream()
                            .filter(s -> Sensor.STATUS_MAINTENANCE.equals(s.getStatus())).count();
        long offline = store.getSensors().values().stream()
                            .filter(s -> Sensor.STATUS_OFFLINE.equals(s.getStatus())).count();

        long totalReadings = store.getSensors().keySet().stream()
                                  .mapToLong(id -> store.getReadings(id).size()).sum();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRooms",    store.getRooms().size());
        stats.put("totalSensors",  store.getSensors().size());
        stats.put("activeSensors",      active);
        stats.put("maintenanceSensors", maint);
        stats.put("offlineSensors",     offline);
        stats.put("totalReadings",      totalReadings);
        stats.put("totalApiRequests",   store.getTotalRequests());
        stats.put("uptimeSeconds",      store.getUptimeSeconds());
        stats.put("timestamp",          System.currentTimeMillis());

        return Response.ok(stats).build();
    }
}

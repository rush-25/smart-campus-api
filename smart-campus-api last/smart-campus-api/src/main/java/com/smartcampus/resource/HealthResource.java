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
 * Health Check Endpoint — GET /api/v1/health  [EXTRA / BONUS FEATURE]
 *
 * Provides a lightweight liveness probe for monitoring systems (e.g., Kubernetes,
 * load balancers, uptime monitors) to check if the API is responsive.
 * Returns 200 OK with runtime statistics when the service is healthy.
 */
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status",         "UP");
        health.put("service",        "Smart Campus API");
        health.put("version",        "1.0.0");
        health.put("uptimeSeconds",  store.getUptimeSeconds());
        health.put("totalRequests",  store.getTotalRequests());
        health.put("rooms",          store.getRooms().size());
        health.put("sensors",        store.getSensors().size());
        health.put("timestamp",      System.currentTimeMillis());
        return Response.ok(health).build();
    }
}

package com.smartcampus.resource;

import com.smartcampus.exception.ValidationException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Search Endpoint — GET /api/v1/search?q=...  [EXTRA / BONUS FEATURE]
 *
 * Cross-entity full-text search across Rooms and Sensors.
 * Allows facility managers to find resources without knowing the exact ID.
 *
 * Example: GET /api/v1/search?q=library
 *   → returns all rooms and sensors whose fields contain "library"
 */
@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response search(@QueryParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Query parameter 'q' is required.");
        }

        String q = query.trim().toLowerCase();

        List<Room> matchedRooms = new ArrayList<>();
        for (Room room : store.getRooms().values()) {
            if (matches(room, q)) matchedRooms.add(room);
        }

        List<Sensor> matchedSensors = new ArrayList<>();
        for (Sensor sensor : store.getSensors().values()) {
            if (matches(sensor, q)) matchedSensors.add(sensor);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("query",          query);
        result.put("totalResults",   matchedRooms.size() + matchedSensors.size());
        result.put("rooms",          matchedRooms);
        result.put("sensors",        matchedSensors);
        result.put("timestamp",      System.currentTimeMillis());

        return Response.ok(result).build();
    }

    private boolean matches(Room r, String q) {
        return contains(r.getId(), q) || contains(r.getName(), q)
            || contains(r.getBuilding(), q) || contains(r.getFloor(), q);
    }

    private boolean matches(Sensor s, String q) {
        return contains(s.getId(), q) || contains(s.getType(), q)
            || contains(s.getStatus(), q) || contains(s.getRoomId(), q)
            || contains(s.getDescription(), q);
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase().contains(q);
    }
}

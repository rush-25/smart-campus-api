package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part 1 – Discovery Endpoint
 *
 * GET /api/v1
 *
 * The entry point for API consumers. Returns metadata and HATEOAS-style
 * links to every top-level resource so clients can navigate without
 * prior knowledge of the full URI structure.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {

        Map<String, Object> response = new LinkedHashMap<>();

        // ── API Metadata ───────────────────────────────────────────
        response.put("apiName",     "Smart Campus Sensor & Room Management API");
        response.put("version",     "1.0.0");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors");
        response.put("author",      "Smart Campus Team");
        response.put("contact",     "smartcampus@university.ac.uk");
        response.put("status",      "Running");

        // ── HATEOAS Links ──────────────────────────────────────────
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self",    "/api/v1");
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        response.put("links", links);

        // ── Available Endpoints ────────────────────────────────────
        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("GET    /api/v1",                        "API discovery (this response)");
        endpoints.put("GET    /api/v1/rooms",                  "List all rooms");
        endpoints.put("POST   /api/v1/rooms",                  "Create a new room");
        endpoints.put("GET    /api/v1/rooms/{id}",             "Get a specific room");
        endpoints.put("DELETE /api/v1/rooms/{id}",             "Delete a room (fails if sensors present)");
        endpoints.put("GET    /api/v1/sensors",                "List all sensors");
        endpoints.put("GET    /api/v1/sensors?type={type}",    "Filter sensors by type (e.g. CO2)");
        endpoints.put("POST   /api/v1/sensors",                "Create a new sensor (roomId required)");
        endpoints.put("GET    /api/v1/sensors/{id}/readings",  "List all readings for a sensor");
        endpoints.put("POST   /api/v1/sensors/{id}/readings",  "Add a new reading to a sensor");
        response.put("endpoints", endpoints);

        return Response.ok(response).build();
    }
}

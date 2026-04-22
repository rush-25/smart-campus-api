package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovery Endpoint — GET /api/v1
 *
 * Returns API metadata: version info, admin contact, and a map of resource URLs.
 * This implements the HATEOAS (Hypermedia As The Engine Of Application State) principle:
 * clients can discover all available resources dynamically from this single entry point
 * rather than relying on static, out-of-date documentation.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @Context
    private UriInfo uriInfo;

    @GET
    public Response discover() {
        String base = uriInfo.getBaseUri().toString() + "api/v1";

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name",        "Smart Campus Sensor & Room Management API");
        response.put("version",     "1.0.0");
        response.put("module",      "5COSC022W - Client-Server Architectures");
        response.put("student",     "M A Rushen Kavindu");
        response.put("studentId",   "w2153204");
        response.put("university",  "University of Westminster");
        response.put("contact",     "w2153204@my.westminster.ac.uk");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors.");
        response.put("timestamp",   System.currentTimeMillis());

        // HATEOAS: advertise all available resource endpoints
        Map<String, String> links = new LinkedHashMap<>();
        links.put("self",     base);
        links.put("rooms",    base + "/rooms");
        links.put("sensors",  base + "/sensors");
        links.put("health",   base + "/health");
        links.put("stats",    base + "/stats");
        links.put("search",   base + "/search?q={query}");
        links.put("export",   base + "/export");
        response.put("_links", links);

        return Response.ok(response).build();
    }
}

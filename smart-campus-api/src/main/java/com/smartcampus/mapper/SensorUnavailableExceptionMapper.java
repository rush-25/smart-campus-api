package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps SensorUnavailableException → HTTP 403 Forbidden.
 *
 * Triggered when a POST /sensors/{id}/readings is attempted
 * against a sensor whose 'active' flag is false.
 *
 * 403 is appropriate here because the sensor exists (not 404)
 * but the server is refusing to service the write request due
 * to the sensor's current operational state.
 */
@Provider
public class SensorUnavailableExceptionMapper
        implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    403);
        body.put("error",     "Forbidden");
        body.put("message",   ex.getMessage());
        body.put("hint",      "Set the sensor's 'active' field to true before submitting readings.");
        body.put("timestamp", Instant.now().toString());

        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

package com.smartcampus.mapper;

import com.smartcampus.exception.ResourceNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps ResourceNotFoundException → HTTP 404 Not Found.
 *
 * @Provider registers this with the JAX-RS runtime.
 * Also explicitly registered in ApplicationConfig for safety.
 */
@Provider
public class ResourceNotFoundExceptionMapper
        implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException ex) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(buildBody(404, "Not Found", ex.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Map<String, Object> buildBody(int status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    status);
        body.put("error",     error);
        body.put("message",   message);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}

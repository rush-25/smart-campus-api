package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps LinkedResourceNotFoundException → HTTP 422 Unprocessable Entity.
 *
 * 422 signals the request URI is valid and the JSON body is syntactically
 * correct, but the content is semantically invalid because a referenced
 * resource (e.g. roomId) does not exist.
 *
 * Note: 422 is absent from the JAX-RS 2.x Response.Status enum,
 * so we supply the numeric code directly.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    422);
        body.put("error",     "Unprocessable Entity");
        body.put("message",   ex.getMessage());
        body.put("hint",      "Ensure all referenced IDs (e.g. roomId) exist before creating this resource.");
        body.put("timestamp", Instant.now().toString());

        return Response
                .status(422)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

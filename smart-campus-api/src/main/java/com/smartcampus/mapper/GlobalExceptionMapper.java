package com.smartcampus.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Catch-all exception mapper.
 * Maps any unhandled Exception → HTTP 500 Internal Server Error.
 *
 * SECURITY NOTE:
 *   The full stack trace is written to the server log ONLY.
 *   The client response body deliberately omits it to prevent
 *   leaking internal class names, library versions, and logic flow
 *   that an attacker could exploit.
 *
 * Register this LAST in ApplicationConfig so more specific mappers
 * take precedence.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER =
            Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception ex) {
        // Full stack trace goes to the server log — never to the client
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by GlobalExceptionMapper", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    500);
        body.put("error",     "Internal Server Error");
        body.put("message",   "An unexpected error occurred. Please contact support if this persists.");
        body.put("timestamp", Instant.now().toString());
        // Stack trace intentionally omitted from response

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

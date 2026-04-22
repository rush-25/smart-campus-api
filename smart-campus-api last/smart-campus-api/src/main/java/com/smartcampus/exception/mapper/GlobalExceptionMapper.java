package com.smartcampus.exception.mapper;

import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global "safety net" Exception Mapper.
 *
 * Catches ANY uncaught Throwable (NullPointerException, ArrayIndexOutOfBoundsException, etc.)
 * and returns a safe HTTP 500 JSON response — WITHOUT leaking any internal stack trace
 * or implementation details to the API consumer.
 *
 * From a cybersecurity perspective, exposing raw Java stack traces to external callers
 * would reveal: package structure, class names, library versions, and internal logic paths —
 * all of which an attacker could exploit to craft targeted exploits.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable ex) {
        // Log the full stack trace server-side for debugging (never sent to client)
        LOG.log(Level.SEVERE, "Unexpected server error: " + ex.getClass().getName(), ex);

        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact the system administrator."
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

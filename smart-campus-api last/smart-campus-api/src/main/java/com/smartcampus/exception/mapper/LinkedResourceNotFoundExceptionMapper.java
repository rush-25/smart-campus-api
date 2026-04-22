package com.smartcampus.exception.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps LinkedResourceNotFoundException → HTTP 422 Unprocessable Entity.
 * Triggered when a sensor is created referencing a roomId that does not exist.
 * 422 is more semantically accurate than 404 here — the request body itself
 * is syntactically valid JSON, but contains an unresolvable reference.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    private static final Logger LOG = Logger.getLogger(LinkedResourceNotFoundExceptionMapper.class.getName());

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        LOG.warning("422 Unprocessable Entity - Linked resource not found: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                422,
                "Unprocessable Entity",
                ex.getMessage()
        );
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

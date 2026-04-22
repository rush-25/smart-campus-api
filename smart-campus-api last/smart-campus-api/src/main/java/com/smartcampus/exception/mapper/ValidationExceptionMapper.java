package com.smartcampus.exception.mapper;

import com.smartcampus.exception.ValidationException;
import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps ValidationException → HTTP 400 Bad Request.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    private static final Logger LOG = Logger.getLogger(ValidationExceptionMapper.class.getName());

    @Override
    public Response toResponse(ValidationException ex) {
        LOG.warning("400 Bad Request - Validation failed: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                400,
                "Bad Request",
                ex.getMessage()
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

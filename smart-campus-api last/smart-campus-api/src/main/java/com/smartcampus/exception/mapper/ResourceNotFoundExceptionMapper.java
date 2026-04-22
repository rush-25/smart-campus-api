package com.smartcampus.exception.mapper;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps ResourceNotFoundException → HTTP 404 Not Found.
 */
@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    private static final Logger LOG = Logger.getLogger(ResourceNotFoundExceptionMapper.class.getName());

    @Override
    public Response toResponse(ResourceNotFoundException ex) {
        LOG.warning("404 Not Found: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage()
        );
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

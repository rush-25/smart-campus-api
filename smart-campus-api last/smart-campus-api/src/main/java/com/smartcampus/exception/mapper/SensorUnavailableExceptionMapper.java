package com.smartcampus.exception.mapper;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps SensorUnavailableException → HTTP 403 Forbidden.
 * Triggered when a POST reading is attempted on a MAINTENANCE sensor.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    private static final Logger LOG = Logger.getLogger(SensorUnavailableExceptionMapper.class.getName());

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        LOG.warning("403 Forbidden - Sensor unavailable: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                403,
                "Sensor Unavailable",
                ex.getMessage()
        );
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

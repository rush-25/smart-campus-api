package com.smartcampus.exception.mapper;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.response.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

/**
 * Maps RoomNotEmptyException → HTTP 409 Conflict.
 * Triggered when a client attempts to delete a room that still has sensors assigned.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    private static final Logger LOG = Logger.getLogger(RoomNotEmptyExceptionMapper.class.getName());

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        LOG.warning("409 Conflict - Room not empty: " + ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                409,
                "Room Conflict",
                ex.getMessage()
        );
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

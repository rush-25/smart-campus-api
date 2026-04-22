package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps RoomNotEmptyException → HTTP 409 Conflict.
 *
 * Triggered when a DELETE /rooms/{id} is attempted while
 * the room still has sensors installed.
 */
@Provider
public class RoomNotEmptyExceptionMapper
        implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    409);
        body.put("error",     "Conflict");
        body.put("message",   ex.getMessage());
        body.put("hint",      "Remove all sensors from this room first, then retry the DELETE.");
        body.put("timestamp", Instant.now().toString());

        return Response
                .status(Response.Status.CONFLICT)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

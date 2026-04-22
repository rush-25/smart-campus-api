package com.smartcampus.resource;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.exception.ValidationException;
import com.smartcampus.model.Room;
import com.smartcampus.response.ApiResponse;
import com.smartcampus.storage.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Room Resource — /api/v1/rooms
 *
 * Manages the lifecycle of campus rooms.
 * Enforces the business rule that a room cannot be deleted while sensors remain assigned.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private static final Logger  LOG   = Logger.getLogger(RoomResource.class.getName());
    private final        DataStore store = DataStore.getInstance();

    // ── GET /api/v1/rooms ─────────────────────────────────────────────────────
    /**
     * Returns the full list of all rooms.
     * Returning full objects (not just IDs) avoids N+1 client requests and reduces
     * round-trips, at the cost of slightly larger payloads.
     */
    @GET
    public Response getAllRooms(@QueryParam("building") String building,
                                @QueryParam("minCapacity") Integer minCapacity) {
        List<Room> rooms = new ArrayList<>(store.getRooms().values());

        // Optional filters (extra feature)
        if (building != null && !building.isEmpty()) {
            rooms.removeIf(r -> r.getBuilding() == null ||
                               !r.getBuilding().equalsIgnoreCase(building));
        }
        if (minCapacity != null) {
            rooms.removeIf(r -> r.getCapacity() < minCapacity);
        }

        return Response.ok(ApiResponse.ok("Retrieved " + rooms.size() + " room(s).", rooms)).build();
    }

    // ── POST /api/v1/rooms ────────────────────────────────────────────────────
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        validateRoom(room);

        if (store.roomExists(room.getId())) {
            throw new ValidationException("A room with ID '" + room.getId() + "' already exists.");
        }

        store.putRoom(room.getId(), room);
        LOG.info("Room created: " + room.getId());

        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location)
                .entity(ApiResponse.ok("Room created successfully.", room))
                .build();
    }

    // ── GET /api/v1/rooms/{roomId} ────────────────────────────────────────────
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) throw new ResourceNotFoundException("Room", roomId);
        return Response.ok(ApiResponse.ok(room)).build();
    }

    // ── PUT /api/v1/rooms/{roomId} — full update (extra feature) ─────────────
    @PUT
    @Path("/{roomId}")
    public Response updateRoom(@PathParam("roomId") String roomId, Room updated) {
        Room existing = store.getRoom(roomId);
        if (existing == null) throw new ResourceNotFoundException("Room", roomId);

        validateRoom(updated);
        updated.setId(roomId);                    // ID is immutable from path
        updated.setSensorIds(existing.getSensorIds()); // preserve sensor links
        store.putRoom(roomId, updated);
        LOG.info("Room updated: " + roomId);

        return Response.ok(ApiResponse.ok("Room updated successfully.", updated)).build();
    }

    // ── DELETE /api/v1/rooms/{roomId} ─────────────────────────────────────────
    /**
     * Deletes a room only if no sensors are assigned.
     *
     * Idempotency: DELETE is idempotent. The FIRST call removes the room → 200.
     * Any SUBSEQUENT call for the same ID finds nothing → throws 404 (ResourceNotFoundException).
     * The outcome is always the same: the room does not exist after the operation.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) throw new ResourceNotFoundException("Room", roomId);

        // Business rule: cannot delete a room that still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }

        store.removeRoom(roomId);
        LOG.info("Room deleted: " + roomId);

        return Response.ok(ApiResponse.ok("Room '" + roomId + "' deleted successfully.", null)).build();
    }

    // ── Validation helper ─────────────────────────────────────────────────────
    private void validateRoom(Room room) {
        if (room == null)
            throw new ValidationException("Request body must not be null.");
        if (room.getId() == null || room.getId().trim().isEmpty())
            throw new ValidationException("id", "Field 'id' is required and must not be blank.");
        if (room.getName() == null || room.getName().trim().isEmpty())
            throw new ValidationException("name", "Field 'name' is required and must not be blank.");
        if (room.getCapacity() <= 0)
            throw new ValidationException("capacity", "Field 'capacity' must be a positive integer.");
    }
}

package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.service.RoomService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/**
 * Part 2 – Room Management Resource
 *
 * Handles all CRUD operations for campus rooms.
 * URI pattern: /api/v1/rooms[/{id}]
 *
 * This class handles only HTTP concerns (request/response mapping).
 * All domain rules live in RoomService.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final RoomService roomService = new RoomService();

    /**
     * GET /rooms
     *
     * Returns all rooms. Never returns 404 — an empty collection is still
     * a valid 200 OK response (absence of data ≠ absence of the resource).
     */
    @GET
    public Response getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return Response.ok(rooms).build();
    }

    /**
     * GET /rooms/{id}
     *
     * Returns a single room by ID.
     * 404 is handled automatically by ResourceNotFoundExceptionMapper.
     */
    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") int id) {
        Room room = roomService.getRoomById(id);
        return Response.ok(room).build();
    }

    /**
     * POST /rooms
     *
     * Creates a new room. The server assigns the ID.
     *
     * Returns:
     *   201 Created  – body contains the full created resource
     *   Location header points to GET /rooms/{newId}
     */
    @POST
    public Response createRoom(Room room) {
        Room created = roomService.createRoom(room);
        URI location = URI.create("/api/v1/rooms/" + created.getId());
        return Response.created(location).entity(created).build();
    }

    /**
     * DELETE /rooms/{id}
     *
     * Deletes a room by ID.
     *
     * Returns:
     *   204 No Content – deletion successful (no body needed)
     *   404 Not Found  – room does not exist
     *   409 Conflict   – room still has sensors (handled by RoomNotEmptyExceptionMapper)
     *
     * DELETE is idempotent in effect (room is gone after any number of calls),
     * though the status code will differ on subsequent calls (204 then 404).
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int id) {
        roomService.deleteRoom(id);
        return Response.noContent().build();   // 204
    }
}

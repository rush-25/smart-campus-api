package com.smartcampus.service;

import com.smartcampus.exception.ResourceNotFoundException;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for Room business logic.
 *
 * Resource classes are kept thin (HTTP concerns only).
 * All domain rules and data access live here.
 */
public class RoomService {

    private final DataStore store = DataStore.getInstance();

    /**
     * Returns every room as an ordered list.
     */
    public List<Room> getAllRooms() {
        return new ArrayList<>(store.getRooms().values());
    }

    /**
     * Fetches a single room by its ID.
     *
     * @throws ResourceNotFoundException (→ 404) if no room with this ID exists.
     */
    public Room getRoomById(int id) {
        Room room = store.getRooms().get(id);
        if (room == null) {
            throw new ResourceNotFoundException("Room not found with ID: " + id);
        }
        return room;
    }

    /**
     * Persists a new room. The server assigns the ID — any client-supplied ID
     * is overwritten to prevent ID collisions.
     *
     * @param room Data from the request body.
     * @return The created room with its server-assigned ID.
     */
    public Room createRoom(Room room) {
        int newId = store.nextRoomId();
        room.setId(newId);
        store.getRooms().put(newId, room);
        return room;
    }

    /**
     * Deletes a room by ID.
     *
     * Business rule: A room may NOT be deleted while it still has sensors
     * installed. This enforces referential integrity at the service layer
     * (substituting for a database foreign-key constraint).
     *
     * @throws ResourceNotFoundException (→ 404) if the room does not exist.
     * @throws RoomNotEmptyException     (→ 409) if sensors are still present.
     */
    public void deleteRoom(int id) {
        // Verify the room exists (throws 404 if not)
        getRoomById(id);

        // Reject deletion if any sensor still references this room
        boolean hasSensors = store.getSensors().values().stream()
                .anyMatch(sensor -> sensor.getRoomId() == id);

        if (hasSensors) {
            throw new RoomNotEmptyException(
                "Cannot delete room " + id + ". It still has sensors installed. "
                + "Remove all sensors from this room before deleting it."
            );
        }

        store.getRooms().remove(id);
    }
}

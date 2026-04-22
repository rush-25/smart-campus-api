package com.smartcampus.exception;

/** Thrown when a room cannot be deleted because it still contains sensors. */
public class RoomNotEmptyException extends RuntimeException {
    private final String roomId;

    public RoomNotEmptyException(String roomId, int sensorCount) {
        super("Room '" + roomId + "' cannot be deleted: it still has " + sensorCount + " sensor(s) assigned.");
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
}

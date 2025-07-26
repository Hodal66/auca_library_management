package com.auca.library.model;

import java.util.Date;
import java.util.UUID;

class Room {
    private UUID roomId;
    private String roomCode;
    private String roomName;
    
    public Room() {
        this.roomId = UUID.randomUUID();
    }
    
    public Room(String roomCode, String roomName) {
        this();
        this.roomCode = roomCode;
        this.roomName = roomName;
    }
    
    // Getters and Setters
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
    
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
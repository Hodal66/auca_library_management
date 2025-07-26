package com.auca.library.model;

import java.util.UUID;

public class User extends Person {
    private UUID userId;
    private String password;
    private Enums.Role role;
    private String userName;
    private UUID villageId;
    
    // Constructors
    public User() {
        super();
        this.userId = UUID.randomUUID();
    }
    
    public User(String firstName, String lastName, Enums.Gender gender, String phoneNumber,
                String userName, String password, Enums.Role role, UUID villageId) {
        super(firstName, lastName, gender, phoneNumber);
        this.userId = UUID.randomUUID();
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.villageId = villageId;
    }
    
    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Enums.Role getRole() { return role; }
    public void setRole(Enums.Role role) { this.role = role; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public UUID getVillageId() { return villageId; }
    public void setVillageId(UUID villageId) { this.villageId = villageId; }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", villageId=" + villageId +
                ", " + super.toString() +
                '}';
    }
}
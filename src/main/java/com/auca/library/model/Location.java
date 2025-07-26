package com.auca.library.model;

import java.util.UUID;

public class Location {
    private String locationCode;
    private UUID locationId;
    private String locationName;
    private Enums.LocationType locationType;
    private UUID parentId;
    
    // Constructors
    public Location() {
        this.locationId = UUID.randomUUID();
    }
    
    public Location(String locationCode, String locationName, Enums.LocationType locationType) {
        this();
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.locationType = locationType;
    }
    
    public Location(String locationCode, String locationName, Enums.LocationType locationType, UUID parentId) {
        this(locationCode, locationName, locationType);
        this.parentId = parentId;
    }
    
    // Getters and Setters
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    
    public UUID getLocationId() { return locationId; }
    public void setLocationId(UUID locationId) { this.locationId = locationId; }
    
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    
    public Enums.LocationType getLocationType() { return locationType; }
    public void setLocationType(Enums.LocationType locationType) { this.locationType = locationType; }
    
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    
    @Override
    public String toString() {
        return "Location{" +
                "locationCode='" + locationCode + '\'' +
                ", locationId=" + locationId +
                ", locationName='" + locationName + '\'' +
                ", locationType=" + locationType +
                ", parentId=" + parentId +
                '}';
    }
}
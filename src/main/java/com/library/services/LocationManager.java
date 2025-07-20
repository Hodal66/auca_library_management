package com.library.services;

import java.util.*;

import com.library.model.Location;
import com.library.model.ELocationType;

public class LocationManager {
    private final List<Location> locations = new ArrayList<>();
    // Create a location with hierarchy
    public Location createLocation(String code, String name, ELocationType type, Location parentLocation) {
        Location location = new Location(code, name, type, parentLocation);
        locations.add(location);
        return location;
    }

    public List<Location> getAllLocations() {
        return Collections.unmodifiableList(locations);
    }

    // Step 2: Get province by village name
    public String getProvinceByVillage(String villageName) {
        for (Location loc : locations) {
            if (loc.getType() == ELocationType.VILLAGE && loc.getName().equalsIgnoreCase(villageName)) {
                Location current = loc;
                // Traverse up the hierarchy to find the province
                while (current != null) {
                    if (current.getType() == ELocationType.PROVINCE) {
                        return current.getName();
                    }
                    current = current.getParentLocation();
                }
            }
        }
        return null; // or throw exception if preferred
    }
}
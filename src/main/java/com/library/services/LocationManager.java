package com.library.services;

import java.util.*;

import com.library.model.Location;

public class LocationManager {
    private final List<Location> locations = new ArrayList<>();
    public Location createLocation(String province, String district, String sector, String cell, String village) {
        Location location = new Location(province, district, sector, cell, village);
        locations.add(location);
        return location;
    }

    public List<Location> getAllLocations() {
        return Collections.unmodifiableList(locations);
    }

    // Step 2: Get province by village name
    public String getProvinceByVillage(String village) {
        for (Location loc : locations) {
            if (loc.getVillage().equalsIgnoreCase(village)) {
                return loc.getProvince();
            }
        }
        return null; // or throw exception if preferred
    }
}
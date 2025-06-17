package com.rotainteligente.datastructures;

import com.rotainteligente.model.CongestionLevel;
import com.rotainteligente.model.Street;

import java.util.HashMap;
import java.util.Map;

public class StreetStatusTable {
    private Map<String, Street> streetMap;

    public StreetStatusTable() {
        this.streetMap = new HashMap<>();
    }

    public void addStreet(Street street) {
        if (street != null && street.getStreetName() != null) {
            this.streetMap.put(street.getStreetName(), street);
        }
    }

    public Street getStreetStatus(String streetName) {
        return this.streetMap.get(streetName);
    }

    public boolean updateStreetStatus(String streetName, CongestionLevel level, boolean hasAccident, boolean isBlocked) {
        Street street = this.streetMap.get(streetName);
        if (street != null) {
            street.setCongestionLevel(level);
            street.setHasAccident(hasAccident);
            street.setBlocked(isBlocked);
            return true;
        }
        return false; // Street not found
    }

    public Map<String, Street> getAllStreets() {
        return new HashMap<>(this.streetMap); // Return a copy to prevent external modification
    }
}

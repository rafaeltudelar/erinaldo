package com.rotainteligente.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData {
    // Simple representation of street connections (graph adjacency list)
    // Key: Street name
    // Value: List of directly connected street names
    private static final Map<String, List<String>> streetConnections = new HashMap<>();

    static {
        // Define connections for the streets simulated in TrafficDataCollector
        // This defines a potential path: A -> B -> E -> F
        // And C is connected to B, D is connected to A
        streetConnections.put("Rua A", Arrays.asList("Rua B", "Rua D"));
        streetConnections.put("Rua B", Arrays.asList("Rua A", "Rua C", "Rua E"));
        streetConnections.put("Rua C", Arrays.asList("Rua B")); // Dead end or connects elsewhere not defined
        streetConnections.put("Rua D", Arrays.asList("Rua A")); // Dead end or connects elsewhere not defined
        streetConnections.put("Rua E", Arrays.asList("Rua B", "Rua F"));
        streetConnections.put("Rua F", Arrays.asList("Rua E")); // End of this particular path segment
    }

    public static List<String> getConnectedStreets(String streetName) {
        return streetConnections.getOrDefault(streetName, Arrays.asList());
    }
}

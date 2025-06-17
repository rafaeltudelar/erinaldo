package com.rotainteligente.core;

import com.rotainteligente.datastructures.MinHeapPriorityQueue;
import com.rotainteligente.datastructures.StreetStatusTable;
import com.rotainteligente.model.PathNode;
import com.rotainteligente.model.Street;
import com.rotainteligente.model.CongestionLevel;

import java.util.*;

public class RoutingEngine {

    private static final double BLOCKED_STREET_WEIGHT = Double.POSITIVE_INFINITY;
    private static final double ACCIDENT_WEIGHT_PENALTY = 100.0; // Arbitrary penalty

    // Calculates the cost/weight of traversing a street
    private double calculateStreetWeight(Street street) {
        if (street == null || street.isBlocked()) {
            return BLOCKED_STREET_WEIGHT;
        }

        double weight = 0.0;
        switch (street.getCongestionLevel()) {
            case LOW:
                weight = 10.0; // Base weight for low congestion
                break;
            case MEDIUM:
                weight = 25.0; // Base weight for medium congestion
                break;
            case HIGH:
                weight = 50.0; // Base weight for high congestion
                break;
        }
        if (street.hasAccident()) {
            weight += ACCIDENT_WEIGHT_PENALTY;
        }
        return weight;
    }

    public List<String> findBestRoute(String startStreetName, String endStreetName, StreetStatusTable statusTable) {
        if (startStreetName == null || endStreetName == null || statusTable == null) {
            return Collections.emptyList();
        }

        MinHeapPriorityQueue priorityQueue = new MinHeapPriorityQueue();
        Map<String, Double> distances = new HashMap<>();       // Stores shortest known distance from start to street
        Map<String, String> predecessors = new HashMap<>();    // Stores predecessor of a street in the shortest path
        Set<String> visitedNodes = new HashSet<>();            // To keep track of visited streets

        // Initialize distances: infinity for all, 0 for startStreet
        for (String streetName : statusTable.getAllStreets().keySet()) {
            distances.put(streetName, Double.POSITIVE_INFINITY);
        }

        Street startStreet = statusTable.getStreetStatus(startStreetName);
        if (startStreet == null) { // Start street not in table
             System.err.println("Start street " + startStreetName + " not found in status table.");
            return Collections.emptyList();
        }
        distances.put(startStreetName, 0.0);

        priorityQueue.addPath(new PathNode(startStreetName, 0.0));

        while (!priorityQueue.isEmpty()) {
            PathNode currentPath = priorityQueue.getNextBestPath();
            String currentStreetName = currentPath.getCurrentNodeIdentifier();

            // If we've already processed this node with a shorter path, skip.
            if (visitedNodes.contains(currentStreetName) && currentPath.getTotalWeight() > distances.get(currentStreetName)) {
                continue;
            }
            visitedNodes.add(currentStreetName);


            // If destination is reached, reconstruct and return path
            if (currentStreetName.equals(endStreetName)) {
                return reconstructPath(predecessors, endStreetName);
            }

            Street currentStreetObject = statusTable.getStreetStatus(currentStreetName);
            if (currentStreetObject == null || currentStreetObject.isBlocked()) {
                continue; // Cannot proceed from a blocked or non-existent street
            }

            // Explore neighbors
            List<String> neighbors = MapData.getConnectedStreets(currentStreetName);
            for (String neighborName : neighbors) {
                Street neighborStreet = statusTable.getStreetStatus(neighborName);
                if (neighborStreet == null) continue; // Neighbor not in status table

                double weightToNeighbor = calculateStreetWeight(neighborStreet);
                if (weightToNeighbor == BLOCKED_STREET_WEIGHT && !neighborName.equals(endStreetName)) { // Allow reaching a blocked destination if it's the target
                    continue;
                }

                // If the destination itself is blocked, its weight is calculated, but we can still "reach" it.
                // For other streets, if blocked, we can't use them as intermediate steps.
                if(neighborStreet.isBlocked() && !neighborName.equals(endStreetName)) continue;


                double newDistToNeighbor = distances.get(currentStreetName) + weightToNeighbor;

                if (newDistToNeighbor < distances.getOrDefault(neighborName, Double.POSITIVE_INFINITY)) {
                    distances.put(neighborName, newDistToNeighbor);
                    predecessors.put(neighborName, currentStreetName);
                    // PathNode constructor taking (currentNodeIdentifier, pathSoFar, totalWeight)
                    // The pathSoFar from currentPath is passed, and new PathNode will add neighborName to it.
                    priorityQueue.addPath(new PathNode(neighborName, currentPath.getPathSoFar(), newDistToNeighbor));
                }
            }
        }
        // If loop finishes, destination was not reachable
        return Collections.emptyList();
    }

    private List<String> reconstructPath(Map<String, String> predecessors, String endStreetName) {
        LinkedList<String> path = new LinkedList<>();
        String current = endStreetName;
        while (current != null) {
            path.addFirst(current);
            current = predecessors.get(current);
            // Safety break for potential cycles if start is not in predecessors (should not happen with Dijkstra)
            if (current != null && path.contains(current)) {
                System.err.println("Error: Cycle detected in path reconstruction or start node not properly linked.");
                return Collections.emptyList(); // Or throw an exception
            }
        }
         // If the first element is not null and is indeed the start of a path from the start node.
        if (!path.isEmpty() && predecessors.containsKey(path.peekFirst()) || path.size() == 1) {
             return path;
        }
        //This means the end node was specified as start node or no path.
        if (path.size() == 1 && !predecessors.containsKey(path.peekFirst())) {
            // If it's just the start node, it means the path is just the start node itself.
            // or if the endStreetName was the startStreetName
            return path;
        }

        return Collections.emptyList(); // Path could not be reconstructed to the implicit start
    }
}

package com.rotainteligente.service;

import com.rotainteligente.datastructures.MinHeapPriorityQueue; // Corrected import
import com.rotainteligente.datastructures.StreetStatusTable;   // Corrected import
import com.rotainteligente.model.PathNode;                     // Corrected import
import com.rotainteligente.model.Street;                       // Corrected import
import com.rotainteligente.model.CongestionLevel;              // Corrected import

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

        priorityQueue.addPath(new PathNode(startStreetName, 0.0)); // PathNode constructor with (startNode, initialWeight)

        while (!priorityQueue.isEmpty()) {
            PathNode currentPath = priorityQueue.getNextBestPath();
            String currentStreetName = currentPath.getCurrentNodeIdentifier();

            // If we've already processed this node with a shorter path, skip.
            // Check distances.get(currentStreetName) instead of currentPath.getTotalWeight() > distances.get(currentStreetName)
            // because currentPath.getTotalWeight() IS distances.get(currentStreetName) if it's the first time we extract this node.
            // The check is more about whether we've already *visited* (i.e. explored from) this node.
            // The visitedNodes set handles this correctly. If already visited, skip.
            if (visitedNodes.contains(currentStreetName)) {
                continue;
            }
            visitedNodes.add(currentStreetName);


            // If destination is reached, reconstruct and return path
            if (currentStreetName.equals(endStreetName)) {
                return reconstructPath(predecessors, endStreetName);
            }

            Street currentStreetObject = statusTable.getStreetStatus(currentStreetName);
            // currentStreetObject should not be null if it's from priorityQueue and distances map,
            // but a check for isBlocked is essential.
            if (currentStreetObject == null || (currentStreetObject.isBlocked() && !currentStreetName.equals(endStreetName)) ) {
                continue; // Cannot proceed from a blocked or non-existent street unless it's the destination
            }

            // Explore neighbors
            List<String> neighbors = MapData.getConnectedStreets(currentStreetName); // MapData is now in the same package
            for (String neighborName : neighbors) {
                Street neighborStreet = statusTable.getStreetStatus(neighborName);
                if (neighborStreet == null) continue; // Neighbor not in status table

                double weightToNeighbor = calculateStreetWeight(neighborStreet);

                // If the destination itself is blocked, its weight is calculated, but we can still "reach" it.
                // For other streets, if blocked (and weight is INF), we can't use them as intermediate steps.
                if (weightToNeighbor == BLOCKED_STREET_WEIGHT && !neighborName.equals(endStreetName)) {
                    continue;
                }


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
        if (!path.isEmpty() && (predecessors.containsKey(path.peekFirst()) || path.size() == 1) ) {
             return path;
        }
        //This means the end node was specified as start node or no path.
        // This check was problematic, simplified: if path.size() == 1, it means start=end, or end is unreachable but was the start.
        // The check `predecessors.containsKey(path.peekFirst())` is true if path has > 1 elements and first element has a predecessor.
        // If path.size() == 1, it means only endStreetName is in path. If it's also the start, pred will be empty.
        // The initial `if` handles this: if path has one element (start=end), `predecessors.containsKey(path.peekFirst())` is false,
        // but `path.size() == 1` is true, so it returns. This is correct.

        return Collections.emptyList(); // Path could not be reconstructed to the implicit start
    }
}

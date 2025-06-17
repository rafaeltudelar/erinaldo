package com.rotainteligente.model;

import java.util.List;
import java.util.ArrayList;

public class PathNode implements Comparable<PathNode> {
    private String currentNodeIdentifier; // Identifier for the current node/street in this path
    private List<String> pathSoFar;       // List of node/street identifiers from start to currentNode
    private double totalWeight;           // Total accumulated weight (e.g., time, distance)

    public PathNode(String currentNodeIdentifier, List<String> pathSoFar, double totalWeight) {
        this.currentNodeIdentifier = currentNodeIdentifier;
        this.pathSoFar = new ArrayList<>(pathSoFar); // Create a new list to ensure immutability of the passed list
        this.pathSoFar.add(currentNodeIdentifier); // Add current node to its own path
        this.totalWeight = totalWeight;
    }

    public PathNode(String startNodeIdentifier, double initialWeight) {
        this.currentNodeIdentifier = startNodeIdentifier;
        this.pathSoFar = new ArrayList<>();
        this.pathSoFar.add(startNodeIdentifier);
        this.totalWeight = initialWeight;
    }


    public String getCurrentNodeIdentifier() {
        return currentNodeIdentifier;
    }

    public List<String> getPathSoFar() {
        return new ArrayList<>(pathSoFar); // Return a copy
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    @Override
    public int compareTo(PathNode other) {
        return Double.compare(this.totalWeight, other.totalWeight);
    }

    @Override
    public String toString() {
        return "PathNode{" +
               "currentNodeIdentifier='" + currentNodeIdentifier + '\'' +
               ", pathSoFar=" + pathSoFar +
               ", totalWeight=" + totalWeight +
               '}';
    }
}

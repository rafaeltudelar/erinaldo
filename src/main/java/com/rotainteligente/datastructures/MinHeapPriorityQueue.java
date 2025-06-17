package com.rotainteligente.datastructures;

import com.rotainteligente.model.PathNode; // Corrected import path
import java.util.PriorityQueue;

public class MinHeapPriorityQueue {
    private PriorityQueue<PathNode> queue;

    public MinHeapPriorityQueue() {
        this.queue = new PriorityQueue<>();
    }

    public void addPath(PathNode pathNode) {
        if (pathNode != null) {
            this.queue.add(pathNode);
        }
    }

    public PathNode getNextBestPath() {
        return this.queue.poll(); // poll() retrieves and removes the head, returns null if empty
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public int size() {
        return this.queue.size();
    }

    public PathNode peek() {
        return this.queue.peek(); // peek() retrieves but does not remove the head, returns null if empty
    }
}

package com.rotainteligente.datastructures;

public class AVLNode<K extends Comparable<K>, V> {
    K key;
    V value;
    int height;
    AVLNode<K, V> left;
    AVLNode<K, V> right;

    public AVLNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.height = 1; // New node is initially added at leaf, height is 1
        this.left = null;
        this.right = null;
    }

    @Override
    public String toString() {
        return "AVLNode{" +
               "key=" + key +
               ", value=" + value +
               ", height=" + height +
               '}';
    }
}

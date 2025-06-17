package com.rotainteligente.datastructures;

public class RouteDecisionTree<K extends Comparable<K>, V> {
    private AVLNode<K, V> root;

    public RouteDecisionTree() {
        this.root = null;
    }

    public AVLNode<K, V> getRoot() {
        return root;
    }

    // Get height of a node
    private int height(AVLNode<K, V> node) {
        return (node == null) ? 0 : node.height;
    }

    // Update height of a node
    private void updateHeight(AVLNode<K, V> node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    // Get balance factor of a node
    private int getBalanceFactor(AVLNode<K, V> node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    // Right rotate subtree rooted with y
    private AVLNode<K, V> rotateRight(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        updateHeight(y);
        updateHeight(x);

        // Return new root
        return x;
    }

    // Left rotate subtree rooted with x
    private AVLNode<K, V> rotateLeft(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        updateHeight(x);
        updateHeight(y);

        // Return new root
        return y;
    }

    // Insert a key-value pair
    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private AVLNode<K, V> insert(AVLNode<K, V> node, K key, V value) {
        // 1. Perform standard BST insertion
        if (node == null) {
            return new AVLNode<>(key, value);
        }

        int compareResult = key.compareTo(node.key);

        if (compareResult < 0) {
            node.left = insert(node.left, key, value);
        } else if (compareResult > 0) {
            node.right = insert(node.right, key, value);
        } else {
            // Duplicate keys are not allowed in this simple AVL tree
            // or update value if allowed: node.value = value;
            return node;
        }

        // 2. Update height of this ancestor node
        updateHeight(node);

        // 3. Get the balance factor of this ancestor node to check if it became unbalanced
        int balance = getBalanceFactor(node);

        // 4. If the node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }

        // Left Right Case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Left Case
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // Return the (unchanged) node pointer
        return node;
    }

    // Find a value by key
    public V find(K key) {
        AVLNode<K, V> resultNode = find(root, key);
        return (resultNode == null) ? null : resultNode.value;
    }

    private AVLNode<K, V> find(AVLNode<K, V> node, K key) {
        if (node == null || key.compareTo(node.key) == 0) {
            return node;
        }

        if (key.compareTo(node.key) < 0) {
            return find(node.left, key);
        } else {
            return find(node.right, key);
        }
    }

    // TODO: Implement delete operation if necessary
    // public void delete(K key) {
    //     root = delete(root, key);
    // }
    // private AVLNode<K,V> delete(AVLNode<K,V> node, K key) { ... }
}

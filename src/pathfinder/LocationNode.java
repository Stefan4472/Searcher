package pathfinder;

import searcher.Node;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

/**
 * A Node used in
 */
public class LocationNode extends Node {

    // coordinates of node
    private float x, y;
    // node's address
    private String address;
    // speed limit at this node todo: implement
    private float speedLimit;
    // nodes accessible via this node. Key is neighbor, value is distance.
    private HashMap<LocationNode, Float> neighbors;

    public LocationNode(float x, float y, String address) {
        this.x = x;
        this.y = y;
        this.address = address;
        neighbors = new HashMap<>(1);
    }

    public void addNeighbor(LocationNode neighbor, float edgeCost) throws NullPointerException {
        if (neighbor == null) {
            throw new NullPointerException("Neighbor can't be null");
        } else {
            neighbors.put(neighbor, edgeCost);
        }
    }

    public Set<LocationNode> getNeighbors() {
        return neighbors.keySet();
    }

    public float getEdgeCost(LocationNode neighbor) {
        if (neighbors.containsKey(neighbor)) {
            return neighbors.get(neighbor);
        } else {
            return Float.MAX_VALUE;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getAddress() {
        return address;
    }

    public boolean equals(LocationNode otherNode) {
        if (this == null || otherNode == null) {
            return false;
        } else if (address.equals(otherNode.getAddress())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return address + " " + x + " " + y;
    }
}

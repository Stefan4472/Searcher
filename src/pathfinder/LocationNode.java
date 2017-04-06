package pathfinder;

import java.util.Enumeration;
import java.util.Hashtable;

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
    private Hashtable<LocationNode, Float> neighbors;

    public LocationNode(float x, float y, String address) {
        this.x = x;
        this.y = y;
        this.address = address;
    }

    public void addNeighbor(LocationNode neighbor, float edgeCost) {
        neighbors.put(neighbor, edgeCost);
    }

    public Enumeration<LocationNode> getNeighbors() {
        return neighbors.keys();
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
}

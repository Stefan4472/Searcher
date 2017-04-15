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
    private int x, y;
    // node's address
    private String address;
    // nodes accessible via this node. Key is neighbor, value is edge.
    private HashMap<LocationNode, Edge> neighbors;

    // creates node with given address and coordinates
    public LocationNode(String address, int x, int y) {
        this.address = address;
        this.x = x;
        this.y = y;
        neighbors = new HashMap<>(1);
    }

    // adds the given node as a neighbor. This means adding it to the map of neighbors
    // along with the given edge, which stores useful data
    public void addNeighbor(LocationNode neighbor, Edge edge) throws NullPointerException {
        if (neighbor == null) {
            throw new NullPointerException("Neighbor can't be null");
        } else {
            neighbors.put(neighbor, edge);
        }
    }

    // returns set of all LocationNodes that this node has edges to
    public Set<LocationNode> getNeighbors() {
        return neighbors.keySet();
    }

    // returns edge to neighbor. Throws NullPointerException if there is no edge to the specified neighbor
    public Edge getEdge(LocationNode neighbor) {
        if (!neighbors.containsKey(neighbor)) {
            throw new NullPointerException("No edge between this node and given neighbor");
        } else {
            return neighbors.get(neighbor);
        }
    }

    // returns edge cost from this node to the given one
    // edge cost is calculated as the time it takes to get to the neighbor,
    // i.e. distance / speedLimit of the edge
    public float getEdgeCost(LocationNode neighbor) {
        return timeTo(neighbor);
    }

    // calculates straight-line distance between given nodes
    public float straightDistanceTo(LocationNode node2) {
        return (float) Math.sqrt(
                (double) (getX() - node2.getX()) * (getX() - node2.getX()) +
                        (getY() - node2.getY()) * (getY() - node2.getY()));
    }

    // calculates time (in seconds) to travel to given node along edge between them
    // returns Integer.MAX_VALUE if no such path exists
    public float timeTo(LocationNode node2) {
        Edge edge = neighbors.get(node2);
        if (edge == null) {
            return Float.MAX_VALUE;
        } else {
            return edge.getDistance() / edge.getSpeedLimit();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
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

    @Override // creates the String representation of the node
    public String toString() {
        return address + "(" + x + "," + y + ")";
    }
}

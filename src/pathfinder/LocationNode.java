package pathfinder;

import com.sun.istack.internal.Nullable;
import searcher.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A LocationNode represents a point on a map at specified coordinates (x,y). It has a unique address, a String,
 * and a list of addresses of neighbors it connects to. It may optionally have a shape, a rectangle
 * that may be drawn onto the map with the specified shapeColor.
 */
public class LocationNode extends Node {

    // coordinates of node
    private int x, y;
    // node's address
    private String address;
    // addresses accessible to this node
    private List<String> neighbors;
    // shape representation on the map
    private Rect shape;
    // shapeColor of shape on the map
    private Color shapeColor;

    // node shapeColor
    private static Color nodeColor = Color.BLACK;
    // edge shapeColor
    private static Color roadColor = Color.GRAY;
    // radius of node (px)
    private static int nodeRadius = 5;


    public static void setNodeColor(Color nodeColor) {
        LocationNode.nodeColor = nodeColor;
    }

    public static void setRoadColor(Color roadColor) {
        LocationNode.roadColor = roadColor;
    }

    public static void setNodeRadius(int nodeRadius) {
        LocationNode.nodeRadius = nodeRadius;
    }

    // creates node with given address and coordinates, as well as optional shape/color
    public LocationNode(String address, int x, int y, @Nullable Rect shape, @Nullable Color shapeColor) {
        this.address = address;
        this.x = x;
        this.y = y;
        neighbors = new LinkedList<>();
        this.shape = shape;
        this.shapeColor = shapeColor;
    }

    // adds the given node as a neighbor. This means adding it to the list of neighbors
    public void addNeighbor(String neighborAddress) {
        neighbors.add(neighborAddress);
    }

    // returns set of all LocationNodes that this node has edges to
    public List<String> getNeighbors() {
        return neighbors;
    }

    // returns edge cost from this node to the given one
    // edge cost is calculated as the time it takes to get to the neighbor,
    // i.e. distance / speedLimit of the edge
//    public float getEdgeCost(LocationNode neighbor) {
//        return timeTo(neighbor);
//    }

    // calculates straight-line distance between given nodes
    public float straightDistanceTo(LocationNode node2) {
        return (float) Math.sqrt(
                (double) (getX() - node2.getX()) * (getX() - node2.getX()) +
                        (getY() - node2.getY()) * (getY() - node2.getY()));
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

    // equals method for comparing to other LocationNodes--just makes sure addresses are equal
    public boolean equals(LocationNode otherNode) {
        if (this == null || otherNode == null) {
            return false;
        } else if (address.equals(otherNode.getAddress())) {
            return true;
        } else {
            return false;
        }
    }

    // todo: override equals and hashcode?

    @Override // creates the String representation of the node
    public String toString() {
        return address + "(" + x + "," + y + ")";
    }
}

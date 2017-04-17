package pathfinder;

import com.sun.istack.internal.Nullable;
import searcher.Node;

import java.awt.*;
import java.util.HashMap;
import java.util.Set;

/**
 * A LocationNode represents a point on a map at specified coordinates (x,y). It has a unique address, a String,
 * and a HashMap of Edges to any neighbors it may have connection to. It may optionally have a shape, a rectangle
 * that may be drawn onto the map with the specified shapeColor.
 */
public class LocationNode extends Node {

    // coordinates of node
    private int x, y;
    // node's address
    private String address;
    // nodes accessible via this node. Key is neighbor, value is edge.
    private HashMap<LocationNode, Edge> neighbors;
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
        neighbors = new HashMap<>(1);
        this.shape = shape;
        this.shapeColor = shapeColor;
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

    // draws the node onto the given graphics object with specified offsets.
    // draws a circle centered at the node's coordinates. If shape != null will
    // draw the shape with the specified shapeColor.
    public void draw(Graphics graphics, int offsetX, int offsetY) { // todo: only draw within clip (shape and edge)
        // draw node
        graphics.setColor(nodeColor);
        graphics.fillOval(getX() - nodeRadius - offsetX, getY() - nodeRadius - offsetY, 2 * nodeRadius, 2 * nodeRadius);

        // draw edges
        graphics.setColor(roadColor);
        for (LocationNode neighbor : getNeighbors()) {
            graphics.drawLine(getX() - offsetX, getY() - offsetY,
                    neighbor.getX() - offsetX, neighbor.getY() - offsetY);
        }

        // draw shape (if has been set)
        if (shape != null) {
            graphics.setColor(shapeColor);
            graphics.drawRect(shape.getX0() - offsetX, shape.getY0() - offsetY, shape.getWidth(), shape.getHeight());
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

    @Override // creates the String representation of the node
    public String toString() {
        return address + "(" + x + "," + y + ")";
    }
}

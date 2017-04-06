import searcher.Node;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class SimpleNode {

    private float edgeCost;
    private List<Node> neighbors;
    private float priorityVal;
    // total edge costs
    private float cost;
    private Node parent;
    private String data;

    public SimpleNode(String data, float edgeCost) {
        this.data = data;
        this.edgeCost = edgeCost;
    }

    public void addNeighbor(Node newNeighbor) {
        if (neighbors == null) {
            neighbors = new LinkedList<Node>();
        }
        neighbors.add(newNeighbor);
    }

    public void setPriorityVal(float priorityVal) {
        this.priorityVal = priorityVal;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getPriorityVal() {
        return priorityVal;
    }

    public String getData() {
        return data;
    }

    public float getEdgeCost() {
        return edgeCost;
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return data + "(" + edgeCost + ")";
    }
}

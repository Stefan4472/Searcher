import java.util.LinkedList;
import java.util.List;

/**
 * The Searcher finds paths through a series of Nodes that must extend this abstract class.
 * This class provides Searcher functionality: it stores the node's priority value, used
 * in the search algorithm, and it records the node's parent, used to retrace the algorithm's
 * steps.
 */
public abstract class Node {

    private float priorityVal;
    private Node parent;

    public Node() {
    }

    public void setPriorityVal(float priorityVal) {
        this.priorityVal = priorityVal;
    }

    public float getPriorityVal() {
        return priorityVal;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}

import java.util.*;

/**
 * Implements an A* search to search through the nodes starting with startNode.
 * Fully visited nodes are put in the closed Hashtable. todo: explanation!
 */
public class Searcher {

    // stores nodes that have been removed from the priority queue
    private Hashtable<Node, Float> visitedNodes;
    // stores nodes that have been found but not yet visited
    private PriorityQueue<Node> unVisitedNodes;
    // comparator used in PriorityQueue--simply compares node priority vals
    private Comparator<Node> queueComparator = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) { // todo: correct value? refine?
            return Float.compare(o1.getPriorityVal(), o2.getPriorityVal());
        }
    };
    // starting node
    private Node startNode;
    // what we're looking for in the "winning" node's getData()
    private String goalState;

    public Searcher(Node startNode, String goalState) {
        this.startNode = startNode;
        this.startNode.setPriorityVal(0);
        this.goalState = goalState;
    }

    // runs search, returning a LinkedList of successive nodes
    // list will be empty if no solution found
    public List<Node> runSearch() {
        visitedNodes = new Hashtable<>();
        unVisitedNodes = new PriorityQueue<>(1, queueComparator);
        unVisitedNodes.add(startNode);
        Node end = search();
        return retracePath(end);
    }

    // recursively calls search until victory or defeat
    // if victory, returns goal node. if defeat, returns null
    private Node search() {
        if (unVisitedNodes.size() == 0) {
            return null;
        } else {
            // get min node
            Node next_min = unVisitedNodes.poll();
            if (visitedNodes.containsKey(next_min)) { // discard if already visited
                return search();
            } else if (next_min.getData().equals(goalState)) {
                return next_min;
            } else {
                for (Node neighbor : next_min.getNeighbors()) {
                    if (!visitedNodes.containsKey(neighbor)) { // todo: account for possible back edges
                        neighbor.setPriorityVal(next_min.getPriorityVal() + neighbor.getEdgeCost() + getHeuristic(neighbor));
                        neighbor.setCost(next_min.getCost() + neighbor.getEdgeCost());
                        neighbor.setParent(next_min);
                        unVisitedNodes.add(neighbor);
                    }
                }
                visitedNodes.put(next_min, next_min.getCost());
                return search();
            }
        }
    }

    // retraces path to get to this node and returns it in a list
    private List<Node> retracePath(Node endNode) {
        List<Node> path = new LinkedList<>();
        Node parent = endNode;
        while (parent != null) {
            path.add(0, parent);
            parent = parent.getParent();
        }
        return path;
    }

    // todo: make way better
    private float getHeuristic(Node node) {
        return 1.0f;
    }

}

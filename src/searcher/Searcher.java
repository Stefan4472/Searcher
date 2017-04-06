package searcher;

import java.util.*;

/**
 * Implements an A* search to search through the nodes starting with startNode.
 * Fully visited nodes are put in the closed Hashtable. todo: explanation!
 */
public class Searcher<T extends Node> {

    // provides "context" to the nodes the Searcher is traversing
    private SearchFramework<T> searchContext;
    // stores nodes that have been removed from the priority queue
    private Hashtable<T, Float> visitedNodes;
    // stores nodes that have been found but not yet visited
    private PriorityQueue<T> unVisitedNodes;
    // comparator used in PriorityQueue--simply compares node priority vals
    private Comparator<T> queueComparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            return Float.compare(o1.getPriorityVal(), o2.getPriorityVal());
        }
    };

    // constructor requires only a searchFramework
    public Searcher(SearchFramework<T> searchContext) {
        this.searchContext = searchContext;
    }

    // runs search from startNode. Returns a List of successive nodes.
    // list will be empty if no solution found
    public List<T> runSearch(T startNode) {
        visitedNodes = new Hashtable<>();
        unVisitedNodes = new PriorityQueue<>(1, queueComparator);
        unVisitedNodes.add(startNode);
        T end = search();
        return retracePath(end);
    }

    // recursively calls search until victory or defeat
    // if victory, returns goal node. if defeat, returns null
    private T search() {
        if (unVisitedNodes.size() == 0) {
            return null;
        } else {
            // get min node
            T next_min = unVisitedNodes.poll();
            if (visitedNodes.containsKey(next_min)) { // discard if already visited
                return search();
            } else if (searchContext.isGoal(next_min)) {
                return next_min;
            } else {
                for (T neighbor : searchContext.getNeighbors(next_min)) {
                    if (!visitedNodes.containsKey(neighbor)) { // todo: account for possible back edges. quick way to check equality
                        neighbor.setPriorityVal(next_min.getPriorityVal() + searchContext.getEdgeCost(next_min, neighbor)
                                + searchContext.getHeuristic(neighbor));
                        neighbor.setParent(next_min);
                        unVisitedNodes.add(neighbor);
                    }
                }
                visitedNodes.put(next_min, next_min.getPriorityVal());
                return search();
            }
        }
    }

    // retraces path to get to this node and returns it in a list
    private List<T> retracePath(T endNode) {
        List<T> path = new LinkedList<>();
        T parent = endNode;
        while (parent != null) {
            path.add(0, parent);
            parent = (T) parent.getParent();
        }
        return path;
    }
}

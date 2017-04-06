package searcher;

import java.util.List;

/**
 * Interface with the methods required for a SearchFramework that
 * helps a Searcher do its job.
 */
public interface SearchFramework<T extends Node> {

    // return list of neighbors node has access to
    List<T> getNeighbors(T node);

    // returns edge cost between the two nodes
    float getEdgeCost(T node1, T node2);

    // returns float value of node's heuristic
    // heuristic must be admissible and consistent
    float getHeuristic(T node);

    // returns whether the given Node meets the goal condition
    boolean isGoal(T node);
}

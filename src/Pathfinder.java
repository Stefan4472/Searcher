import java.util.Collections;
import java.util.List;

/**
 * Extends SearchFramework and provides a "map" of nodes with coordinates and addresses
 * to be searched.
 */
public class Pathfinder implements SearchFramework<LocationNode> {

    private LocationNode startNode;
    private LocationNode goalNode;

    public Pathfinder(LocationNode startNode, LocationNode goalNode) { // todo: we need an index of addresses
        this.startNode = startNode;
        this.goalNode = goalNode;
    }

    @Override
    public List<LocationNode> getNeighbors(LocationNode node) {
        return Collections.list(node.getNeighbors()); // todo: improve
    }

    @Override
    public float getEdgeCost(LocationNode node1, LocationNode node2) {
        return node1.getEdgeCost(node2);
    }

    @Override // use straight-line distance to goalNode as heuristic todo: improve, factor in speed limits
    public float getHeuristic(LocationNode node) {
        return (float) Math.sqrt(
                (double) (node.getX() - goalNode.getX()) * (node.getX() - goalNode.getX()) +
                    (node.getY() - goalNode.getY()) * (node.getY() - goalNode.getY()));
    }

    @Override
    public boolean isGoal(LocationNode node) {
        return node.getAddress().equals(goalNode.getAddress());
    }
}

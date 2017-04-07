package pathfinder;

/**
 * Stores information about an edge between two LocationNodes: distance, street name, and speed limit.
 */
public class Edge {

    private float distance;
    private String streetName;
    private float speedLimit;

    public Edge(float distance, String streetName, float speedLimit) {
        this.distance = distance;
        this.streetName = streetName;
        this.speedLimit = speedLimit;
    }

    public float getDistance() {
        return distance;
    }

    public String getStreetName() {
        return streetName;
    }

    public float getSpeedLimit() {
        return speedLimit;
    }
}

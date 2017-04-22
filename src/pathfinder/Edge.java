package pathfinder;

/**
 * Stores information about an edge between two addresses: distance, street name, and speed limit. Can be one way,
 * in which case travel is only permitted
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

    // returns time to travel across this edge
    public float getTime() {
        return distance / speedLimit;
    }
}

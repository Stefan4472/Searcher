package pathfinder;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Utility methods
 */
public class MapUtil {

    // Constructs a Map from data in the specified file. Data must be structured in the correct way
    // (see Map.java header). Throws IOException if file cannot be found.
    // Throws IllegalArgumentException if contents of file cannot be parsed correctly
    public static Map loadMap(String fileName) throws IOException, IllegalArgumentException {
        Map map = new Map();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int num_nodes = Integer.parseInt(br.readLine());
            int num_edges = Integer.parseInt(br.readLine());
            int total_lines = num_nodes + num_edges;

            String[] line_tokens;
            LocationNode node, node2;
            String address, street_name;
            float speed_limit, distance;
            int x, y;
            for (int i = 0; i < total_lines; i++) {
                line_tokens = br.readLine().split(" "); // todo: use addEdge, addNode methods
                if (i < num_nodes) { // create node and add to addresses
                    address = line_tokens[0];
                    x = Integer.parseInt(line_tokens[1]);
                    y = Integer.parseInt(line_tokens[2]);
                    map.addNode(address, x, y);
                } else { // access specified nodes and set edge cost
                    node = map.getNode(line_tokens[0]);
                    node2 = map.getNode(line_tokens[1]);
                    map.addEdge(line_tokens[0], line_tokens[1], line_tokens[2], Float.parseFloat(line_tokens[3]));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // writes address-node pairs to file followed by all edges with edge costs
    // throws IOException if there was an error writing the file
    public boolean saveToFile(String fileName) throws IOException {
        /*try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(addresses.size() + "\n");
            writer.write(numEdges + "\n");
            // write in all node data
            for (String address : addresses.keySet()) {
                writer.write(addresses.get(address) + "\n");
            }
            // write in all edge data
            LocationNode node;
            Edge edge;
            for (String address : addresses.keySet()) {
                node = addresses.get(address);
                for (LocationNode neighbor : node.getNeighbors()) {
                    edge = node.getEdge(neighbor);
                    writer.write(node.getAddress() + " " + neighbor.getAddress() + " " +
                            edge.getStreetName() + " " + edge.getSpeedLimit() + "\n");
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }*/
        return false;
    }
    // generates directions for the navigator from the current node index in the path
    // to the next one. Example: "Head NorthEast along Sunset Ave."
    public static String getDirections(List<LocationNode> path, int currNodeIndex) {
        if (currNodeIndex == path.size() - 1) {
            return "Destination Reached";
        } else {
            LocationNode current = path.get(currNodeIndex);
            LocationNode approaching = path.get(currNodeIndex + 1);
            String direction = "Head ";
            // figure out compass direction. Remember: canvas coordinates!
            if (approaching.getY() - current.getY() > 0) {
                direction += "South";
            } else if (approaching.getY() - current.getY() < 0) {
                direction += "North";
            }
            if (approaching.getX() - current.getX() > 0) {
                direction += "East";
            } else if (approaching.getX() - current.getX() < 0) {
                direction += "West";
            }
            direction += " along " + current.getEdge(approaching).getStreetName();
            return direction;
        }
    }

    // calculates time required to navigate from node at startIndex to node at endIndex along the given path (in seconds)
    // throws IllegalArgumentException if startIndex > endIndex or either is out of range of the given path
    public static float calculateTime(List<LocationNode> path, int startIndex, int endIndex) throws IllegalArgumentException{
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("StartIndex can't be greater than EndIndex");
        } else if (startIndex < 0 || endIndex < 0 || startIndex >= path.size() || endIndex >= path.size()) {
            throw new IllegalArgumentException("Error: index out of path bounds");
        } else {
            float time = 0;
            LocationNode a, b;
            for (int i = 0; i < endIndex; i++) {
                a = path.get(i);
                b = path.get(i + 1);
                time += a.timeTo(b);
            }
            return time;
        }
    }

    // calculates distance to navigate from node at startIndex to node at endIndex along the given path (in pixels)
    // throws IllegalArgumentException if startIndex > endIndex or either is out of range of the given path
    public static int calculateDistance(List<LocationNode> path, int startIndex, int endIndex) throws IllegalArgumentException{
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("StartIndex can't be greater than EndIndex");
        } else if (startIndex < 0 || endIndex < 0 || startIndex >= path.size() || endIndex >= path.size()) {
            throw new IllegalArgumentException("Error: index out of path bounds");
        } else {
            int distance = 0;
            LocationNode a, b;
            for (int i = 0; i < endIndex; i++) {
                a = path.get(i);
                b = path.get(i + 1);
                distance += a.getEdge(b).getDistance();
            }
            return distance;
        }
    }
}

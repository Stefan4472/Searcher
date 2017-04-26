package pathfinder;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Utility methods
 */
public class MapUtil {

    /* Constructs a Map from data in the specified file. Data must be structured in the following way:
     *
     * Map File Format:
     * First line states number of nodes (n)
     * Second line states number of edges (e)
     * This is followed by n lines in the space-separated format "Address x-coordinate y-coordinate *shape-x0 shape-y0 shape-xf shape-yf shape-color* (optional)" // todo: give nodes ID plus address?
     * This is followed by e lines in the space-separated format "Address1 Address2 StreetName SpeedLimit"
     *
     * The fields defining shape coordinates and color are optional.
     *
     * Throws IOException if file cannot be found.
     * Throws IllegalArgumentException if contents of file cannot be parsed correctly
     */
    public static Map loadMap(String fileName) throws IOException, IllegalArgumentException {
        Map map = new Map();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int num_nodes = Integer.parseInt(br.readLine());
            int num_edges = Integer.parseInt(br.readLine());
            int total_lines = num_nodes + num_edges;

            String[] line_tokens;
            String address;
            int x, y;
            for (int i = 0; i < total_lines; i++) {
                line_tokens = br.readLine().split(" "); // todo: use addEdge, addNode methods
                if (i < num_nodes) { // create node and add to addresses
                    address = line_tokens[0];
                    x = Integer.parseInt(line_tokens[1]);
                    y = Integer.parseInt(line_tokens[2]);
                    Rect shape = null;
                    Color color = null;
                    // check if more tokens--in which case pase shape specifications
                    if (line_tokens.length > 3) {
                        shape = new Rect(
                                Integer.parseInt(line_tokens[3]),
                                Integer.parseInt(line_tokens[4]),
                                Integer.parseInt(line_tokens[5]),
                                Integer.parseInt(line_tokens[6])
                                );
                        color = Color.decode(line_tokens[7]);
                    }
                    map.addNode(address, x, y);
                } else { // access specified nodes and set edge cost
                    map.addEdge(line_tokens[0], line_tokens[1], line_tokens[2], Float.parseFloat(line_tokens[3]));
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Couldn't read specified file \"" + fileName + "\"");
            System.exit(0);
        } catch (NumberFormatException|NullPointerException|ArrayIndexOutOfBoundsException e) {
            System.out.print("Error parsing file. Ensure correct number of addresses and edges have been declared,\n" +
                    "and that they are defined in the proper way.");
            System.exit(0);
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
    // to the next one. Example: "Head NorthEast along Sunset Ave." Map object is used to look up the Edge
    public static String getDirections(List<LocationNode> path, int currNodeIndex, Map map) {
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
            direction += " along " + map.getEdge(current, approaching).getStreetName();
            return direction;
        }
    }

    // calculates time required to navigate from node at startIndex to node at endIndex along the given path (in seconds).
    // throws IllegalArgumentException if startIndex > endIndex or either is out of range of the given path.
    // uses Map object to look up edges.
    public static float calculateTime(List<LocationNode> path, int startIndex, int endIndex, Map map) throws IllegalArgumentException{
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("StartIndex can't be greater than EndIndex");
        } else if (startIndex < 0 || endIndex < 0 || startIndex >= path.size() || endIndex >= path.size()) {
            throw new IllegalArgumentException("Error: index out of path bounds");
        } else {
            float time = 0;
            for (int i = startIndex; i < endIndex; i++) {
                time += map.getEdge(path.get(i), path.get(i + 1)).getTime();
            }
            return time;
        }
    }

    // calculates distance to navigate from node at startIndex to node at endIndex along the given path (in pixels).
    // throws IllegalArgumentException if startIndex > endIndex or either is out of range of the given path.
    // Uses the given Map object to look up edges in the path.
    public static int calculateDistance(List<LocationNode> path, int startIndex, int endIndex, Map map) throws IllegalArgumentException{
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("StartIndex can't be greater than EndIndex");
        } else if (startIndex < 0 || endIndex < 0 || startIndex >= path.size() || endIndex >= path.size()) {
            throw new IllegalArgumentException("Error: index out of path bounds");
        } else {
            int distance = 0;
            for (int i = 0; i < endIndex; i++) {
                distance += map.getEdge(path.get(i), path.get(i + 1)).getDistance();
            }
            System.out.println("Distance from " + path.get(startIndex) + " to " + path.get(endIndex) + " is " + distance);
            return distance;
        }
    }
}

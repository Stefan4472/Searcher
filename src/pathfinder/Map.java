package pathfinder;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Represents a street map in coordinate space. Each node is indexed by its address in the addresses HashMap.
 * Each edge is one-way and defined by the two LocationNodes it spans. Each edge has a name and a
 * speedlimit (used in calculating edge costs).
 *
 * The Map can be read in from a file (in a determined format) and saved to a file.
 *
 * Map File Format:
 * First line states number of nodes (n)
 * Second line states number of edges (e)
 * This is followed by n lines in the space-separated format "Address x-coordinate y-coordinate" // todo: give nodes ID plus address?
 * This is followed by e lines in the space-separated format "Address1 Address2 StreetName SpeedLimit"
 */
public class Map {

    // stores (address, node) pairs
    private HashMap<String, LocationNode> addresses = new HashMap<>();
    //
    // number of edges
    private int numEdges;

    // empty constructor
    public Map() {
    }

    // returns node with specified address
    public LocationNode getNode(String address) {
        return addresses.get(address);
    }

    // creates node from given information and stores it
    public void addNode(String address, int x, int y) {
        addresses.put(address, new LocationNode(address, x, y));
    }

    // takes the two given addresses and creates an edge from address1 to address2 with the
    // given properties. Throws NullPointerException if node specified by address doesn't exist.
    public void addEdge(String address1, String address2, String streetName, float speedLimit) throws NullPointerException {
        if (!addresses.containsKey(address1)) {
            throw new NullPointerException("The given address \"" + address1 + "\" is invalid");
        } else if  (!addresses.containsKey(address2)) {
            throw new NullPointerException("The given address \"" + address2 + "\" is invalid");
        } else {
            LocationNode node1 = addresses.get(address1);
            LocationNode node2 = addresses.get(address2);
            // calculate distance between the nodes
            float distance = node1.distanceTo(node2);
            node1.addNeighbor(addresses.get(address2), new Edge(distance, streetName, speedLimit));
            numEdges++;
        }
    }

    // takes the name of a file in the root directory todo: rename static loadMapFromFile?
    // constructs address/node table from data in given file
    // throws IOException if file cannot be found
    // throws IllegalArgumentException if contents of file cannot be parsed correctly
    public Map(String fileName) throws IOException, IllegalArgumentException {
        addresses = new HashMap<>();
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
                line_tokens = br.readLine().split(" ");
                if (i < num_nodes) { // create node and add to addresses
                    address = line_tokens[0];
                    x = Integer.parseInt(line_tokens[1]);
                    y = Integer.parseInt(line_tokens[2]);
                    node = new LocationNode(address, x, y);
                    addresses.put(address, node);
                } else { // access specified nodes and set edge cost
                    node = addresses.get(line_tokens[0]);
                    node2 = addresses.get(line_tokens[1]);
                    distance = node.distanceTo(node2);
                    street_name = line_tokens[2];
                    speed_limit = Float.parseFloat(line_tokens[3]);
                    node.addNeighbor(node2, new Edge(distance, street_name, speed_limit));
                    addresses.put(line_tokens[0], node);
                }
            }
            br.close();
            // set numEdges for future reference
            numEdges = num_edges;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes address-node pairs to file followed by all edges with edge costs
    // throws IOException if there was an error writing the file
    public boolean saveToFile(String fileName) throws IOException {
        try {
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
        }
    }

    private Color backgroundColor = Color.GREEN;
    private Color nodeColor = Color.BLACK;
    private Color roadColor = Color.GRAY;
    private Color pathColor = Color.BLUE;

    // The draw method draws a specified portion (clip) of the map onto the given Graphics object.
    // The clip is a rectangle, and will be translated to (0,0) of the drawFrame.
    // e.g. the clip is (70, 100, 100, 150). This method will draw any nodes and edges that fall
    // within the boundaries of the clip, translating them to (0,0). // todo: clips
    // The path stack is a list of adjacent nodes that define a path in the order given.
    // Edges between the nodes in this list will be drawn in pathColor.
    public void drawClip(Graphics drawFrame, Rect clip, List<LocationNode> path) {
        // draw background
        drawFrame.setColor(backgroundColor);
        drawFrame.fillRect(0, 0, clip.getWidth(), clip.getHeight());

        // get the offset, the amount subtracted off each coordinate to draw it to the screen. This is to achieve a
        // scrolling behavior as the clip changes
        int offX = clip.getX0(), offY = clip.getY0();

        ((Graphics2D) drawFrame).setStroke(new BasicStroke(1));

        // traverse the nodes
        for (LocationNode address : addresses.values()) { // todo: this will not draw edges from nodes that are off-screen
            // check if the node is in the clip
            if (clip.containsPoint(address.getX(), address.getY())) {
                // draw node
                drawFrame.setColor(nodeColor);
                drawFrame.fillOval(address.getX() - 5 - offX, address.getY() - 5 - offY, 10, 10);

                // draw edges
                drawFrame.setColor(roadColor);
                for (LocationNode neighbor : address.getNeighbors()) {
                    drawFrame.drawLine(address.getX() - offX, address.getY() - offY,
                            neighbor.getX() - offX, neighbor.getY() - offY);
                }
            }
        }

        // draw the edges between the nodes specified in path
        drawFrame.setColor(pathColor);
        ((Graphics2D) drawFrame).setStroke(new BasicStroke(2));
        LocationNode node, next_node = path.get(0);
        for (int i = 0; i < path.size() - 1; i++) {
            node = next_node;
            next_node = path.get(i + 1);
            // draw edge only if one of the nodes is in the clip
            if (clip.containsPoint(node.getX(), node.getY()) || clip.containsPoint(next_node.getX(), next_node.getY())) {
                drawFrame.drawLine(node.getX() - offX, node.getY() - offY,
                        next_node.getX() - offX, next_node.getY() - offY);
            }
        }
    }
}

package pathfinder;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

/**
 * Represents a geographical map in coordinate space. Stores a table of all addresses with their corresponding
 * LocationNodes. Can be read in from a file (in a determined format) and saved to a file.
 *
 * Map File Format:
 * First line states number of nodes (n)
 * Second line states number of edges (e)
 * This is followed by n lines in the space-separated format "Address x-coordinate y-coordinate"
 * This is followed by e lines in the space-separated format "Address1 Address2 EdgeCost"
 * where there is a path between Address1 and Address2 that has the given edge cost
 */
public class Map {

    // stores address, node pairs
    private HashMap<String, LocationNode> addresses; // todo: addAddress method, constructor that builds the file from scratch
    // stores number of edges
    private int numEdges;

    // returns node with specified address
    public LocationNode getNode(String address) {
        return addresses.get(address);
    }

    private Color backgroundColor = Color.GREEN;
    private Color nodeColor = Color.BLACK;
    private Color roadColor = Color.GRAY;
    // draws the map onto the given Graphics2D object todo: assurances, better stuff, scrolling
    public void draw(Graphics drawFrame) {
        ((Graphics2D) drawFrame).setStroke(new BasicStroke(3));
        drawFrame.setColor(backgroundColor);
        drawFrame.fillRect(0, 0, 200, 200);
        for (LocationNode address : addresses.values()) {
            // draw node
            drawFrame.setColor(nodeColor);
            drawFrame.fillOval((int) address.getX() - 5, (int) address.getY() - 5, 10, 10);
            drawFrame.setColor(roadColor);
            // draw edges
            for (LocationNode neighbor : address.getNeighbors()) {
                drawFrame.drawLine((int) address.getX(), (int) address.getY(), (int) neighbor.getX(), (int) neighbor.getY());
            }
        }
    }

    // takes the name of a file in the root directory todo: rename loadMapFromFile
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
            for (int i = 0; i < total_lines; i++) {
                line_tokens = br.readLine().split(" ");
                if (i < num_nodes) { // create node and add to addresses
                    node = new LocationNode(Float.parseFloat(line_tokens[1]), Float.parseFloat(line_tokens[2]), line_tokens[0]);
                    addresses.put(line_tokens[0], node);
                } else { // access specified nodes and set edge cost
                    node = addresses.get(line_tokens[0]);
                    node2 = addresses.get(line_tokens[1]);
                    node.addNeighbor(node2, Float.parseFloat(line_tokens[2]));
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
    public boolean saveToFile(String fileName) throws IOException { // todo: finish
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(addresses.size() + "\n");
            writer.write(numEdges + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

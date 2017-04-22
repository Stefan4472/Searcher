package pathfinder;

import com.sun.istack.internal.Nullable;
import searcher.SearchFramework;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a street map in coordinate space. Each node is indexed by its address in the addresses HashMap.
 * Each edge is defined by the two addresses it spans. Two addresses can define at most one edge. Each edge has a
 * name and a speedlimit (used in calculating edge costs).
 *
 * The Map can be read in from a file (in a determined format) and saved to a file via the MapUtil loadMap
 * and saveMap methods.
 *
 * The Map functions as a SearchFramework, implementing methods that are used by the Searcher for pathfinding.
 */
public class Map implements SearchFramework<LocationNode> {

    // stores (address, node) pairs
    private HashMap<String, LocationNode> addresses = new HashMap<>();
    // stores MapSector with list of addresses contained in it
    private HashMap<MapSector, List<String>> sectors = new HashMap<>();
    // stores all edges in the map. Key is an AddressTuple, value is an Edge object
    private HashMap<AddressTuple, Edge> edges = new HashMap<>();
    //
    // number of edges
    private int numEdges;
    // node to be reached in goal state of navigation
    private LocationNode goalNode;
    // empty constructor
    public Map() {
    }

    @Override // returns neighbors of given node
    public List<LocationNode> getNeighbors(LocationNode node) { // todo: performance?
        return node.getNeighbors().stream()
                .map(address -> addresses.get(address))
                .collect(Collectors.toList());
    }

    // looks up edge corresponding to the two given LocationNodes, converting them into an AddressTuple for use
    // with getEdge(AddressTuple). Throws NoSuchElementException if no such edge exists.
    public Edge getEdge(LocationNode node1, LocationNode node2) throws NoSuchElementException {
        return getEdge(new AddressTuple(node1.getAddress(), node2.getAddress()));
    }

    // looks up edge corresponding to given AddressTuple. Throws NoSuchElementException if none exists
    public Edge getEdge(AddressTuple endNodes) throws NoSuchElementException {
        if (!edges.containsKey(endNodes)) {
            throw new NoSuchElementException("No Such Edge Exists");
        } else {
            return edges.get(endNodes);
        }
    }

    @Override // returns edge cost to get from node1 to node2 (time)
    public float getEdgeCost(LocationNode node1, LocationNode node2) {
        Edge edge = edges.get(new AddressTuple(node1.getAddress(), node2.getAddress()));
        if (edge == null) { // return max value if no edge exists between the two
            return Float.MAX_VALUE;
        } else {
            return edge.getDistance() / edge.getSpeedLimit();
        }
    }

    @Override // heuristic used to guide A* search of the map. Uses straight-line distance to goal-node.
    // throws IllegalStateException if no goalNode has been set todo: improve, factor in speed limits
    public float getHeuristic(LocationNode node) throws IllegalStateException {
        if (goalNode == null) {
            throw new IllegalStateException("Map must have a goal set before it can be searched");
        } else {
            return node.straightDistanceTo(goalNode);
        }
    }

    @Override // checks whether the given node is the goal state, i.e. the address we're searching for.
    // does this by checking whether the addresses match up (addresses are assumed to be unique)
    // throws IllegalStateException if goalNode hasn't been set
    public boolean isGoal(LocationNode node) throws IllegalStateException {
        if (goalNode == null) {
            throw new IllegalStateException("Map must have a goal set before it can be searched");
        } else {
            return node.getAddress().equals(goalNode.getAddress());
        }
    }

    // returns node with specified address
    public LocationNode getNode(String address) {
        return addresses.get(address);
    }

    // sets address of node we're searching for if we were to use a Searcher
    // throws NoSuchElementException if there is no node for the given address
    public void setGoal(String address) throws NoSuchElementException {
        if (addresses.containsKey(address)) {
            goalNode = addresses.get(address);
        } else {
            throw new NoSuchElementException("Given address \"" + address + "\" does not exist");
        }
    }

    // creates node from given information and stores it in addresses map. Also determines sector it is in and records
    // that in the sectors map.
    public void addNode(String address, int x, int y, @Nullable Rect shape, @Nullable Color color) {
        addresses.put(address, new LocationNode(address, x, y, shape, color));
        MapSector sector = MapSector.getSector(addresses.get(address));
        if (!sectors.containsKey(sector)) {
            sectors.put(sector, new LinkedList<>()); // todo: shapes hashmap and HashMap<Sector, List<address>> shapes
        }
        sectors.get(sector).add(address);
    }

    // takes the two given addresses. Registers the existence of the edge under an AddressTuple in the edges HashMap.
    // Pairs this with an Edge object built with the given streetName and speedLimit. Throws NullPointerException if
    // an address is encountered that hasn't already been registered via addNode().
    public void addEdge(String address1, String address2, String streetName, float speedLimit) throws NullPointerException {
        if (!addresses.containsKey(address1)) {
            throw new NullPointerException("The given address \"" + address1 + "\" is invalid");
        } else if  (!addresses.containsKey(address2)) {
            throw new NullPointerException("The given address \"" + address2 + "\" is invalid");
        } else {
            LocationNode node1 = addresses.get(address1);
            LocationNode node2 = addresses.get(address2);
            edges.put(new AddressTuple(address1, address2), new Edge(node1.straightDistanceTo(node2), streetName, speedLimit));
            numEdges++;
        }
    }

    private Color backgroundColor = Color.GREEN;
//    private Color nodeColor = Color.BLACK;
//    private Color roadColor = Color.GRAY;
    private Color pathColor = Color.BLUE;

    // The draw method draws a specified portion (clip) of the map onto the given Graphics object.
    // The clip is a rectangle, and will be translated to (0,0) of the drawFrame.
    // e.g. the clip is (70, 100, 100, 150). This method will draw any nodes and edges that fall
    // within the boundaries of the clip, translating them to (0,0). // todo: clips. This may be drawing beyond the screen
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

        LocationNode node;

        // get the MapSectors intersected by the given clip
        for (MapSector sector : MapSector.getIntersectedSectors(clip)) {
            // traverse the nodes within each sector
            if (sectors.containsKey(sector)) {
                for (String address : sectors.get(sector)) { // todo: this will not draw edges from nodes that are off-screen
                    node = addresses.get(address);
                    // check if the node is in the clip
                    if (clip.containsPoint(node.getX(), node.getY())) {
                        node.draw(drawFrame, offX, offY);
//                        // draw node
//                        drawFrame.setColor(nodeColor);
//                        drawFrame.fillOval(node.getX() - 5 - offX, node.getY() - 5 - offY, 10, 10);
//
//                        // draw edges
//                        drawFrame.setColor(roadColor);
//                        for (LocationNode neighbor : node.getNeighbors()) {
//                            drawFrame.drawLine(node.getX() - offX, node.getY() - offY,
//                                    neighbor.getX() - offX, neighbor.getY() - offY);
//                        }
                    }
                }
            }
        }

        // draw the edges between the nodes specified in path
        drawFrame.setColor(pathColor);
        ((Graphics2D) drawFrame).setStroke(new BasicStroke(2));
        LocationNode next_node = path.get(0);
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

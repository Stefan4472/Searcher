package pathfinder;

import com.sun.istack.internal.Nullable;
import searcher.SearchFramework;

import java.awt.*;
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
    // stores all edges in the map. Key is an AddressTuple, value is an Edge object
    private HashMap<AddressTuple, Edge> edges = new HashMap<>();
    // stores MapSector with list of addresses contained in it
    private HashMap<MapSector, List<String>> sectorNodes = new HashMap<>();
    // stores MapSector with list of AddressTuples defining Edges that pass through the sector
    private HashMap<MapSector, List<AddressTuple>> sectorEdges = new HashMap<>();
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
            return edge.getTime();
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
    // that in the sectorNodes map.
    public void addNode(String address, int x, int y) throws DuplicateKeyException {
        if (addresses.containsKey(address)) {
            throw new DuplicateKeyException("Key \"" + address + "\" has already been registered and cannot be added twice");
        } else {
            addresses.put(address, new LocationNode(address, x, y, null, null));
            MapSector sector = MapSector.getSector(addresses.get(address));
            if (!sectorNodes.containsKey(sector)) {
                sectorNodes.put(sector, new LinkedList<>()); // todo: shapes hashmap and HashMap<Sector, List<address>> shapes
            }
            sectorNodes.get(sector).add(address);
        }
    }

    // takes the two given addresses. Registers the existence of the edge under an AddressTuple in the edges HashMap.
    // Pairs this with an Edge object built with the given streetName and speedLimit. Throws NullPointerException if
    // an address is encountered that hasn't already been registered via addNode().
    public void addEdge(String address1, String address2, String streetName, float speedLimit) throws NullPointerException {
        LocationNode node1 = addresses.get(address1);
        LocationNode node2 = addresses.get(address2);
        if (node1 == null) {
            throw new NullPointerException("The given address \"" + address1 + "\" is invalid");
        } else if (node2 == null) {
            throw new NullPointerException("The given address \"" + address2 + "\" is invalid");
        } else {
            // update both nodes neighbor lists as well as the edges HashMap
            node1.addNeighbor(address2);
            node2.addNeighbor(address1);
            AddressTuple tuple = new AddressTuple(address1, address2);
            edges.put(tuple, new Edge(node1.straightDistanceTo(node2), streetName, speedLimit));

            // determine which Sectors this edge intersects and register these in the sectorEdges HashMap
            List<MapSector> intersected = MapSector.getIntersectedSectors(node1, node2);
            for (MapSector sector : intersected) {
                if (!sectorEdges.containsKey(sector)) {
                    sectorEdges.put(sector, new LinkedList<>()); // todo: shapes hashmap and HashMap<Sector, List<address>> shapes
                }
                sectorEdges.get(sector).add(tuple);
            }
        }
    }

    private Color backgroundColor = Color.GREEN;
    private Color nodeColor = Color.BLACK;
    private Color roadColor = Color.GRAY;
    private Color pathColor = Color.BLUE;
    private int nodeRadius = 5;

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
        int offsetX = clip.getX0(), offsetY = clip.getY0();

        ((Graphics2D) drawFrame).setStroke(new BasicStroke(1));

        LocationNode node1, node2;

        // get the MapSectors intersected by the given clip
        for (MapSector sector : MapSector.getIntersectedSectors(clip)) {
            // draw the nodes in each sector
            if (sectorNodes.containsKey(sector)) {
                drawFrame.setColor(nodeColor);
                for (String address : sectorNodes.get(sector)) { // todo: this will not draw edges from nodes that are off-screen
                    node1 = addresses.get(address);
                    // check if the node is in the clip
                    if (clip.containsPoint(node1.getX(), node1.getY())) {
                        // draws the node onto the given graphics object with specified offsets.
                        // draws a circle of nodeRadius centered at the node's coordinates (minus offsets)
                        drawFrame.fillOval(node1.getX() - nodeRadius - offsetX, node1.getY() - nodeRadius - offsetY, 2 * nodeRadius, 2 * nodeRadius);
                    }
                }
            }
            // draw the edges in each sector
            if (sectorEdges.containsKey(sector)) {
                drawFrame.setColor(roadColor);
                for (AddressTuple address_tuple : sectorEdges.get(sector)) {
                    node1 = addresses.get(address_tuple.getAddress1());
                    node2 = addresses.get(address_tuple.getAddress2());
                    drawFrame.drawLine(node1.getX() - offsetX, node1.getY() - offsetY,
                            node2.getX() - offsetX, node2.getY() - offsetY);
                }
            }
            // draw shape (if has been set)
//                        if (shape != null) {
//                            drawFrame.setColor(shapeColor);
//                            drawFrame.drawRect(shape.getX0() - offsetX, shape.getY0() - offsetY, shape.getWidth(), shape.getHeight());
//                        }

            // draw the edges between the nodes specified in path
            drawFrame.setColor(pathColor);
            ((Graphics2D) drawFrame).setStroke(new BasicStroke(2));
            LocationNode next_node = path.get(0);
            AddressTuple edge_addresses;
            for (int i = 0; i < path.size() - 1; i++) {
                node1 = next_node;
                next_node = path.get(i + 1);
                edge_addresses = new AddressTuple(node1.getAddress(), next_node.getAddress());
                // draw edge only if one of the nodes is in the clip todo: clip edge drawings
                if (clip.containsPoint(node1.getX(), node1.getY()) || clip.containsPoint(next_node.getX(), next_node.getY())) {
                    drawFrame.drawLine(node1.getX() - offsetX, node1.getY() - offsetY,
                            next_node.getX() - offsetX, next_node.getY() - offsetY);
                }
            }
        }
    }
}

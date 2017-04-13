package pathfinder;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Displays a Map in a simple Jframe. The map display has the special feature that it can animate an icon "navigating"
 * a specified path of nodes. For this feature to be active, the path field must be set and navigating must be turned
 * to true. While navigating, a triangle icon will follow along the given path at the speed specified by the edge it is
 * currently on. There will be a readout that displays the directions to the next node, street name of edge being
 * traversed, speed limit, distance traveled, and time remaining until destination.
 */
public class MapDisplay extends JPanel {

    // map to be displayed
    private Map map;
    // path of nodes to highlight
    private List<LocationNode> path;
    // whether map is navigating between nodes in the path
    private boolean navigating;
    // screen dimensions
    private int screenWidth, screenHeight;

    // set to true when destination of path has been reached (if navigating)
    private boolean destinationReached;
    // used for animating navigation
    private int currNodeIndex;
    // frame counter for frames elapsed while traversing current edge
    private int framesThisEdge;
    // total frames to be elapsed while traversing current edge
    private int totalFramesThisEdge;
    // coordinates of pointer
    private float currentX, currentY;
    // degrees to rotate pointer
    private float pointerAngle; // todo
    // street name of edge being traversed
    private String currentStreetName;
    // directions for getting to the next node from the current one
    private String directions;
    // speed limit of current edge being traversed
    private float currentSpeedLimit;
    // distance and time remaining in path, respectively
    private float distanceRemaining, timeRemaining;
    // distance travelled so far
    private float distanceTravelled;

    // number of times to refresh map per second
    private static final int FPS = 40;

    public MapDisplay(Map map, int screenWidth, int screenHeight) {
        this.map = map;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        path = new LinkedList<>();
        ActionListener repaint = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                update();
                repaint();
            }
        };
        Timer timer = new Timer(1000 / FPS, repaint);
        timer.start();
    }

    // updates all fields and pointer
    public void update() {
        if (navigating) {
            if (framesThisEdge == totalFramesThisEdge) { // we have reached the next node
                currNodeIndex++;
                framesThisEdge = 0;
                // check if destination has been reached
                if (currNodeIndex == path.size() - 1) {
                    destinationReached = true;
                    System.out.println("Destination Reached");
                } else { // switch to navigating to next node
                    System.out.println("Switched to node " + currNodeIndex);
                    // reset frame counters for this edge
                    totalFramesThisEdge = (int) calculateTime(path, currNodeIndex, currNodeIndex + 1) * FPS;
                    // update all edge-relevant fields
                    directions = getDirections(path, currNodeIndex);
                    currentSpeedLimit = path.get(currNodeIndex).getEdge(path.get(currNodeIndex + 1)).getSpeedLimit();
                    currentStreetName = path.get(currNodeIndex).getEdge(path.get(currNodeIndex + 1)).getStreetName();
                }
            }
            if (!destinationReached) { // todo: issues with distance and time calculations
                framesThisEdge++;
                // get pointers to previous and next node, as well as edge being travelled
                LocationNode last_node = path.get(currNodeIndex);
                LocationNode next_node = path.get(currNodeIndex + 1);
                Edge edge = last_node.getEdge(next_node);
                // update pointer and all readout data based on frames travelled this edge / total frames to be travelled
                float change_x = (next_node.getX() - last_node.getX()) * ((float) framesThisEdge / totalFramesThisEdge);
                float change_y = (next_node.getY() - last_node.getY()) * ((float) framesThisEdge / totalFramesThisEdge);
                currentX = last_node.getX() + change_x;
                currentY = last_node.getY() + change_y;
//                distanceTravelled += edge.getSpeedLimit() / FPS;
//                distanceRemaining -= edge.getSpeedLimit() / FPS;
                distanceTravelled += change_x + change_y;
                distanceRemaining -= change_x - change_y;
                timeRemaining -= 1.0f / FPS;
            }
        }
    }

    private Rect clip;
    // used to calculate dimensions of text drawn by Graphics. Only one font is used.
    private FontMetrics fontMetrics;
    // paints the map to the JPanel
    public void paintComponent(Graphics g) {
        // calculate the clip. This is a Rect with width and height of the screen where (currentX, currentY) is at
        // its center. todo: test, improve
        clip = new Rect(Math.max(0, (int) currentX - screenWidth / 2), Math.max(0, (int) currentY - screenHeight / 2),
                Math.max(screenWidth, (int) currentX + screenWidth / 2), Math.max(screenHeight, (int) currentY + screenHeight / 2));
        // draw the map with path
        map.drawClip(g, clip, path);
        // get fontMetrics if you haven't already
        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics();
        }
        // draw the layout
        if (navigating) {
            g.setColor(Color.BLUE);
            // draw the pointer
            g.fillOval((int) currentX - 5, (int) currentY - 5, 10, 10);
            // draw the directions centered in width and 2/3 of the way down the screen in height
            g.drawString(directions, screenWidth / 2 - fontMetrics.stringWidth(directions) / 2,
                    screenHeight * 2 / 3 + fontMetrics.getHeight());
            g.drawString(currentX + "," + currentY, 0, 20);
            g.drawString(distanceTravelled + "," + distanceRemaining, 0, 40);
            g.drawString(timeRemaining + "", 0, 60);
            g.drawString(currentStreetName + "," + currentSpeedLimit, 0, 80);
        }
    }

    // gives a path of nodes to navigate and sets navigating to true.
    // throws IllegalArgumentException if path has fewer than two nodes, path is invalid
    // (nodes are not adjacent), or MapDisplay was previously navigating and had not been
    // turned off
    public void startNavigation(List<LocationNode> path) throws IllegalArgumentException {
        if (navigating) {
            throw new IllegalStateException("Navigating must be set to false before path may be changed");
        } else if (path == null || path.size() < 2) {
            throw new IllegalArgumentException("Path must have at least two elements");
        } else {
            navigating = true;
            this.path = path;

            // calculate total distance and time to traverse path
            timeRemaining = calculateTime(path, 0, path.size() - 1);
            distanceRemaining = calculateDistance(path, 0, path.size() - 1);
            System.out.println("Remaining: " + timeRemaining + "," + distanceRemaining);

            // set init values for starting navigation
            currNodeIndex = 0;
            framesThisEdge = 0;
            totalFramesThisEdge = (int) (calculateTime(path, 0, 1) * FPS);
            currentX = path.get(0).getX();
            currentY = path.get(0).getY();
            currentStreetName = path.get(0).getEdge(path.get(1)).getStreetName();
            directions = getDirections(path, 0);
            distanceTravelled = 0;
        }
    }

    // will stop drawing the path to the screen and animating navigation
    public void stopNavigation() {
        navigating = false;
    }

    // generates directions for the navigator from the current node index in the path
    // to the next one. Example: "Head NorthEast along Sunset Ave."
    private static String getDirections(List<LocationNode> path, int currNodeIndex) {
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
    private static float calculateTime(List<LocationNode> path, int startIndex, int endIndex) throws IllegalArgumentException{
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
    private static int calculateDistance(List<LocationNode> path, int startIndex, int endIndex) throws IllegalArgumentException{
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

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

    private float pathDistance, pathTime;

    // used for animating navigation
    private int currNodeIndex;
    // frame counter for frames elapsed while traversing current edge
    private int framesThisEdge;
    // total frames to be elapsed while traversing current edge
    private int totalFramesThisEdge;
    // coordinates of pointer
    private float currentX, currentY;
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

    public MapDisplay(Map map) {
        setPreferredSize(new Dimension(200, 200));
        this.map = map;
        path = new Stack<>();
        ActionListener repaint = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                update();
                repaint();
            }
        };
        Timer timer = new Timer(1000 / FPS, repaint);
        timer.start();
    }

    public void update() {
        if (navigating) {

        }
    }

    public void paintComponent(Graphics g) {
        map.drawClip(g, null, path);
    }
    // sets the path to be used for navigation, if enabled.
    // throws IllegalStateException if the path is changed while the Map is currently navigating
    // (navigation must be turned off first) or if the path has less than two elements
    public void setPath(List<LocationNode> path) throws IllegalStateException {
        if (navigating) {
            throw new IllegalStateException("Navigating must be set to false before path may be changed");
        } else if (path == null || path.size() < 2) {
            throw new IllegalStateException("Path must have at least two elements");
        }
    }

    public void setNavigating(boolean navigating) throws IllegalStateException {
        if (navigating && path == null || path.isEmpty()) {
            throw new IllegalStateException("A path must be specified before navigation can begin");
        } else if (navigating) {
            // calculate total distance and time to traverse path
            timeRemaining = calculateTime(path, 0, path.size() - 1);
            distanceRemaining = calculateDistance(path, 0, path.size() - 1);
            // set init values for starting navigation
            currNodeIndex = 0;
            framesThisEdge = 0;
            totalFramesThisEdge = (int) calculateTime(path, 0, 1) / FPS;
            currentX = path.get(0).getX();
            currentY = path.get(0).getY();
            currentStreetName = path.get(0).getEdge(path.get(1)).getStreetName();
            directions = getDirections(path, 0);
            distanceTravelled = 0;
        }
        // update the variable
        this.navigating = navigating;
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
            if (approaching.getY() - current.getY() > 0) {
                direction += "North";
            } else if (approaching.getY() - current.getY() < 0) {
                direction += "South";
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
            for (int i = 0; i < endIndex - 1; i++) {
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
            for (int i = 0; i < endIndex - 1; i++) {
                a = path.get(i);
                b = path.get(i + 1);
                distance += a.getEdge(b).getDistance();
            }
            return distance;
        }
    }
}

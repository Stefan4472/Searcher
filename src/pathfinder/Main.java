package pathfinder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Stefan on 4/6/2017.
 */
public class Main {

    private static String GOAL_STATE = "GOAL!";
    public static void main(String[] args) {
        Node start = buildPath();
        Searcher searcher = new Searcher(start, GOAL_STATE);
        List<Node> path = searcher.runSearch();
        if (path == null) {
            System.out.println("No path found");
        } else {
            System.out.println(listToArray(path));
        }
    }

    private static Node buildPath() {
        Node start = new Node("start", 0);
        Node b = new Node("b", 10);
        Node c = new Node("c", 5);
        Node d = new Node("d", 3);
        Node goal = new Node(GOAL_STATE, 4);
        start.addNeighbor(b);
        b.addNeighbor(c);
        b.addNeighbor(d);
        c.addNeighbor(d);
        d.addNeighbor(goal);
        return start;
    }

    private static String listToArray(List<?> list) {
        String to_string = "[";
        for (int i = 0; i < list.size(); i++) {
            to_string += list.get(i).toString();
            if (i < list.size() - 1) {
                to_string += " ";
            }
        }
        return to_string + "]";
    }
}

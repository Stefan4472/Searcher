package pathfinder;

import searcher.Searcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Stefan on 4/6/2017.
 */
public class Main {

    private static String GOAL_STATE = "GOAL!";

    public static void main(String[] args) {
        try {
            Map map = new Map("mapfile.txt");
//            LocationNode start = buildPath();
//            Pathfinder map =
//                    Searcher searcher = new Searcher(start, GOAL_STATE);
//            List<LocationNode> path = searcher.runSearch();
//            if (path == null) {
//                System.out.println("No path found");
//            } else {
//                System.out.println(listToArray(path));
//            }
        } catch (IOException e) {

        }
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

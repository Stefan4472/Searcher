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

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage is [text file] [start address] [goal address]");
            System.exit(0);
        }
        try {
            Map map = new Map(args[0]);
            LocationNode start = map.getNode(args[1]);
            LocationNode goal = map.getNode(args[2]);
            Pathfinder pathfinder = new Pathfinder(goal);
            Searcher<LocationNode> searcher = new Searcher<>(pathfinder);
            List<LocationNode> path = searcher.runSearch(start);
            if (path == null) {
                System.out.println("No path found");
            } else {
                System.out.println(listToArray(path));
            }
        } catch (IOException e) {
            System.out.println("File not found");
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

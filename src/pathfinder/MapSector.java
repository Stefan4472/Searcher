package pathfinder;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a square portion of the map. The map is broken up into a grid of these MapSectors, each of which has
 * a row and column index. Includes convenience methods for determining sector(s) a node, edge, or region would intersect.
 */
public class MapSector { // todo: lots of testing

    // width of every MapSector's bounds
    private static final int WIDTH = 300;

    // index of this sector with respect to the map as a whole. Bounds are calculated as
    // (row * WIDTH, col * WIDTH, (row + 1) * WIDTH, (col + 1) * WIDTH)
    int row, col;

    private MapSector(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // returns MapSector instance that the given node would be in
    // Uses getSector(int, int) method using node's coordinates
    public static MapSector getSector(LocationNode node) {
        return getSector(node.getX(), node.getY());
    }

    // returns MapSector instance that the given coordinates would be in
    private static MapSector getSector(int x, int y) {
        return new MapSector(x / WIDTH, y / WIDTH);
    }

    // returns list of all MapSectors that intersect the given Rect
    // Accomplishes this by calculating the sectors of the top left and bottom right points.
    // If these are equal, the top left sector fits the whole region.Otherwise, we add all the sectors
    // from top_left(i,j) to bottom_right(i,j)
    public static List<MapSector> getIntersectedSectors(Rect region) {
        List<MapSector> intersected = new LinkedList<>();
        MapSector top_left = getSector(region.getX0(), region.getY0());
        MapSector bottom_right = getSector(region.getX1(), region.getY1());
        // done if it's the same sector
        if (top_left.equalsSector(bottom_right)) {
            intersected.add(top_left);
            return intersected;
        } else { // add all sectors between top_left and bottom_right
            for (int i = top_left.row; i <= bottom_right.row; i++) {
                for (int j = top_left.col; j <= bottom_right.col; j++) {
                    intersected.add(new MapSector(i, j));
                }
            }
            return intersected;
        }

    }
    // returns list of all MapSectors that a path from node to node2 would intersect
    // Accomplishes this by building the Rect region from node to node2 and calling
    // getIntersectedSectors(Rect) on it.
    public static List<MapSector> getIntersectedSectors(LocationNode node, LocationNode node2) {
        return getIntersectedSectors(new Rect(node.getX(), node.getY(), node2.getX(), node2.getY()));
    }

    @Override // returns true if given object is a MapSector with same row, col
    public boolean equals(Object object) {
        if (object == null) {
            throw new NullPointerException("Can't be null");
        } else if (!(object instanceof MapSector)) {
            return false;
        } else {
            return ((MapSector) object).row == row && ((MapSector) object).col == col;
        }
    }

    public boolean equalsSector(MapSector sector) {
        return row == sector.row && col == sector.col;
    }

    /*@Override // todo: may need to customize to ensure all that matters is row and col
    public int hashCode() {
        return hashCode();
    }*/

    @Override
    public String toString() {
        return "Sector(" + row + "," + col + ")";
    }
}

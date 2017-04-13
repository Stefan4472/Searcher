package pathfinder;

/**
 * Represents a 2-dimensional rectangle with integer coordinates. Defined by the top-left point (x0,y0) and the
 * width and height. Constructor takes the top-left point with a width and a height. Coordinates of bottom-right
 * point (x1,y1) can be calculated on demand.
 */
public class Rect {

    private int x0, y0, width, height;

    public Rect(int x0, int y0, int width, int height) {
        this.x0 = x0;
        this.y0 = y0;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    public int getX1() {
        return x0 + width;
    }

    public int getY1() {
        return y0 + height;
    }
}

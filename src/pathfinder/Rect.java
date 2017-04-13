package pathfinder;

/**
 * Represents a 2-dimensional rectangle with integer coordinates. Defined by the top-left point (x0,y0) and the
 * bottom-right point (x1, y1). Constructor takes the top-left point with a width and a height.
 */
public class Rect {

    private int x0, y0, x1, y1;

    public Rect(int x0, int y0, int width, int height) {
        this.x0 = x0;
        this.y0 = y0;
        x1 = x0 + width;
        y1 = y0 + height;
    }

    public int getWidth() {
        return x1 - x0;
    }

    public int getHeight() {
        return y1 - y0;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }
}

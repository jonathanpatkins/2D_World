package byow.Core;

/**
 * A useful class that can be used for many calculations in generation.
 * @author Jonathan Atkins, Jake Webster 11/12/20.
 * @source inspired by TA implementation of Hexagon points in lab 12.
 */
public class Position {
    /**
     * @param x: The x coordinate.
     * @param y: The y coordinate.
     */
    private int x;
    private int y;

    /**
     * Sets x to @param x1.
     * Sets y to @param y1.
     */
    public Position(int x1, int y1) {
        this.x = x1;
        this.y = y1;
    }

    /**
     * Alternate constructor.
     * Modifies the @param x of Position @param p by @param xOffset.
     * Modifies the @param y of Position @param p by @param yOffset.
     */
    public Position(Position p, int xOffset, int yOffset) {
        x = p.getX() + xOffset;
        y = p.getY() + yOffset;
    }

    /**
     * @Return @param x.
     */
    public int getX() {
        return x;
    }

    /**
     * @Return @param y.
     */
    public int getY() {
        return y;
    }

    /**
     * @Return a simple string representing the class.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * @Return whether this Position equals Position @param o.
     */
    @Override
    public boolean equals(Object o) {
        Position other = (Position) o;
        if (other == null) {
            return false;
        }
        return x == other.getX() && y == other.getY();
    }

    /**
     * @Return a simple hashCode for the Position.
     */
    @Override
    public int hashCode() {
        return 17 * x * y;
    }
}

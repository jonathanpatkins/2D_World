package byow.Core;
import java.util.List;

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
     * Sets x to @param x.
     * Sets y to @param y.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
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

    /**
     * TODO: Remove?
     * @return the horizontal distance between this and @param other
     * If negative, it means this is further right than @param other.
     * If positive, it means this is to the left of @param other.
     * This can be helpful in tunnel generation.
     */
    public int horizontalDistance(Position other) {
        return other.getX() - x;
    }

    /**
     * TODO: Remove?
     * @return the vertical distance between this and @param other.
     * If negative, it means this is above @param other.
     * If positive, it means this is below @param other.
     */
    public int verticalDistance(Position other) {
        return other.getY() - y;
    }

    /**
     * TODO: Remove?
     * @return the distance between this and @param other.
     * This is not the TRUE distance between them, but distance considering we
     * can only move up, down, left, and right.
     */
    public int distance(Position other) {
        int xDist = Math.abs(horizontalDistance(other));
        int yDist = Math.abs(verticalDistance(other));
        return xDist + yDist;
    }

    /**
     * TODO: Remove.
     * @return the nearest Position from the list @param positions.
     * Application: this could be used to find the two nearest doors to each other.
     * Runs in theta(N), we could use KdTree but with such a small number of doors
     * I don't think it is necessary.
     */
    public Position nearest(List<Position> positions) {
        if (positions.size() == 0) {
            return null;
        }
        Position near = positions.get(0);
        int dist = this.distance(near);
        for (int i = 0; i < positions.size(); i += 1) {
            Position newPos = positions.get(i);
            int newDist = this.distance(newPos);
            if (newDist < dist) {
                near = newPos;
            }
        }
        return near;
    }
}

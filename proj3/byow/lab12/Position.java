package byow.lab12;
import java.util.List;

public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position p, int xOffset, int yOffset) {
        x = p.getX() + xOffset;
        y = p.getY() + yOffset;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        Position other = (Position) o;
        if (other == null) {
            return false;
        }
        return x == other.getX() && y == other.getY();
    }

    /**
     * @return the distance between this and @param other.
     * This is not the TRUE distance between them, but distance considering we
     * can only move up, down, left, and right.
     */
    public int distance(Position other) {
        int xDist = Math.abs(other.getX() - this.getX());
        int yDist = Math.abs(other.getY() - this.getY());
        return xDist + yDist;
    }

    /**
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
        if (this.equals(new Position(51, 17))) {
            //System.out.println("Door: " + this.toString());
        }
        for (int i = 0; i < positions.size(); i += 1) {
            Position newPos = positions.get(i);
            int newDist = this.distance(newPos);
            if (this.equals(new Position(51, 17))) {
                //System.out.println("Other: " + newPos.toString() + " Dist: " + newDist);
            }
            if (newDist < dist) {
                near = newPos;
            }
        }
        if (this.equals(new Position(51, 17))) {
            //System.out.println("Near: " + near);
        }
        return near;
    }
}

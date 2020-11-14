package byow.lab12;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;

public class Hexagon {
    private int side;
    private Position upCorner, lowRight, lowLeft;
    private TETile tile;
    public Hexagon(int length, Position upperLeft, TETile t) {
        side = length;
        upCorner = upperLeft;
        lowLeft = new Position(upperLeft, -side, -side);
        lowRight = new Position(lowLeft, getRowWidth(side), 0);
        tile = t;
    }

    //Get positions the hexagon occupies
    //Ex. width = 80, height = 30
    // upperLeft = (40, 20) (41, 20) (42, 20), ... (39, 19)...
    public List<Position> getPositions() {
        List<Position> result = new ArrayList<>();
        for (int y = 0; y < getHeight(); y += 1) {
            int startX = getRowStart(y);
            int widthX = getRowWidth(y);
            for (int x = 0; x < widthX; x += 1) {
                Position p = new Position(startX + x, upCorner.getY() + y);
                result.add(p);
            }
        }
        return result;
    }

    private int getHeight() {
        return side * 2;
    }

    public TETile getTile() {
        return tile;
    }

    private int getRowStart(int row) {
        if (row < side) {
            return upCorner.getX() - row;
        } else {
            return upCorner.getX() - (side - 1) + (row % side);
        }
    }

    public int getRowWidth(int row) {
        if (row < side) {
            return side + 2 * row;
        } else {
            return side + (side - 1) * 2 - (row % side) * 2;
        }
    }

    public Position getLowLeft() {
        return lowLeft;
    }

    public Position getLowRight() {
        return lowRight;
    }

    public Position getUpCorner() {
        return upCorner;
    }
}

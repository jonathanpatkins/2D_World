package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class that generates rooms and keeps track of key elements of the Room.
 * @author Jonathan Atkins, Jake Webster 11/15/20.
 */
public class Room  implements java.io.Serializable {
    /**
     * @param upperLeft: The upper left position just outside the room.
     * @param upperRight: The upper right position just outside the room.
     * @param lowerRight: The lower right position just outside the room.
     * @param lowerLeft: The lower left position of the room and building block for the rest.
     */
    private Position upperLeft, upperRight, lowerLeft, lowerRight;

    /**
     * @param wallLocation: The positions of each wall tile of the RoomAdj.
     * @param floorLocation: The positions of each floor tile of the RoomAdj.
     * @param cornerLocation: The positions of the corner tiles of the RoomAdj.
     * @param adjLocation: The position tiles adjacent to the RoomAdj.
     */
    private List<Position> wallLocation, floorLocation, cornerLocation, adjLocation;

    /**
     * @param tileWall and @param tileFloor are used for the aesthetics of the RoomAdj.
     */
    private TETile tileWall, tileFloor;

    /**
     * @param height and @param width are the randomly generated dimensions of the room.
     */
    private int height, width;

    /**
     * @param lower sets this classes @param lowerLeft.
     * @param r is the Random used for random generation of the room.
     * @param tileF sets this classes @param tileFloor
     * @param tileW sets this classes @param tileWall.
     * @source This class was generally inspired by Hexagon.java from the TA's
     *         implementation of Lab12.
     */
    public Room(Position lower, Random r, TETile tileF, TETile tileW) {

        this.lowerLeft = lower;
        this.tileFloor = tileF;
        this.tileWall = tileW;

        // Generate width (x) and height (y).
        this.width = RandomUtils.uniform(r, 3, 15);
        this.height = RandomUtils.uniform(r, 3, 15);

        // Generate upperLeft, upperRight, lowerRight from length and width.
        this.upperLeft = new Position(lowerLeft, 0, height);
        this.lowerRight = new Position(lowerLeft, width, 0);
        this.upperRight = new Position(lowerLeft, width, height);

        // Generate the positions of the walls and floor.
        this.wallLocation = new ArrayList<>();
        this.floorLocation = new ArrayList<>();
        this.adjLocation = new ArrayList<>();
        this.cornerLocation = new ArrayList<>();

        this.getPositions();
    }


    /**
     * For a given room, records the positions of the outside edges, walls, and floor.
     */
    private void getPositions() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Position pos = new Position(this.lowerLeft, j, i);
                // If top, bottom, left, or right row, it is a wall.
                if (isWall(j, i)) {
                    // If it is a corner, add it to the corner list.
                    if (isCorner(j, i)) {
                        this.cornerLocation.add(pos);
                    }
                    this.wallLocation.add(pos);
                } else {
                    this.floorLocation.add(pos);
                }
            }
        }
    }

    /**
     * @Return whether the point (@param x, @param y) could be considered a corner.
     */
    private boolean isCorner(int x, int y) {
        if ((y == 0 && (x == 0 || x == this.width - 1))
                || (y == this.height - 1 && (x == 0 || x == this.width - 1))) {
            return true;
        }
        return false;
    }

    /**
     * @Return whether the point (@param x, @param y) could be considered a wall.
     */
    private boolean isWall(int x, int y) {
        return (y == 0 || y == this.height - 1) || (x == 0 || x == this.width - 1);
    }

    /**
     * @Return @param tileFloor.
     */
    public TETile getTileFloor() {
        return tileFloor;
    }

    /**
     * @Return @param tileWall.
     */
    public TETile getTileWall() {
        return tileWall;
    }

    /**
     * @Return the list of floor locations @param floorLocation.
     */
    public List<Position> getFloorLocation() {
        return floorLocation;
    }

    /**
     * @Return the list of wall locations @param wallLocation.
     */
    public List<Position> getWallLocation() {
        return wallLocation;
    }

    /**
     * @Return the list of adjacent locations @param adjLocation.
     */
    public List<Position> getAdjLocation() {
        return adjLocation;
    }

    /**
     * @Return the list of corner locations @param cornerLocation.
     */
    public List<Position> getCornerLocation() {
        return cornerLocation;
    }

    /**
     * @return the lowerLeft position.
     */
    public Position getLowerLeft() {
        return lowerLeft;
    }

    /**
     * @return the upperRight position.
     */
    public Position getUpperRight() {
        return upperRight;
    }

    /**
     * @return the upperLeft position.
     */
    public Position getUpperLeft() {
        return upperLeft;
    }

    /**
     * @return the lowerRight position.
     */
    public Position getLowerRight() {
        return lowerRight;
    }

    /**
     * @Return a simple String representation of the Room.
     */
    @Override
    public String toString() {
        return "Lower left: " + lowerLeft + " , Upper right: " + upperRight;
    }
}

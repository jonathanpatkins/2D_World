package byow.Core;

import byow.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class that generates rooms and keeps track of key elements of the Room.
 * @author Jonathan Atkins, Jake Webster 11/15/20.
 */
public class RoomAdj {
    /**
     * @param upperLeft: The upper left position just outside the room.
     * @param upperRight: The upper right position just outside the room.
     * @param lowerRight: The lower right position just outside the room.
     * @param lowerLeft: The lower left position of the room and building block for the rest.
     */
    private Position upperLeft, upperRight, lowerRight, lowerLeft;

    /**
     * @param doorLocation: TODO: remove if necessary.
     * @param wallLocation: The positions of each wall tile of the RoomAdj.
     * @param floorLocation: The positions of each floor tile of the RoomAdj.
     * @param cornerLocation: The positions of the corner tiles of the RoomAdj.
     * @param adjLocation: The position tiles adjacent to the RoomAdj.
     */
    private List<Position> doorLocation, wallLocation, floorLocation, cornerLocation, adjLocation;

    /**
     * @param tileWall and @param tileFloor are used for the aesthetics of the RoomAdj.
     */
    private TETile tileWall, tileFloor;

    /**
     * @param height and @param width are the randomly generated dimensions of the room.
     */
    private int height, width;

    /**
     * @param lowerLeft sets this classes @param lowerLeft.
     * @param r is the Random used for random generation of the room.
     * @param tileFloor sets this classes @param tileFloor
     * @param tileWall sets this classes @param tileWall.
     * @source This class was generally inspired by Hexagon.java from the TA's
     *         implementation of Lab12.
     */
    public RoomAdj(Position lowerLeft, Random r, TETile tileFloor, TETile tileWall) {
        this.lowerLeft = lowerLeft;
        this.tileFloor = tileFloor;
        this.tileWall = tileWall;

        this.width = RandomUtils.uniform(r, 3, 15);
        this.height = RandomUtils.uniform(r, 3, 15);

        this.upperLeft = new Position(lowerLeft, 0, height);
        this.lowerRight = new Position(lowerLeft, width, 0);
        this.upperRight = new Position(lowerLeft, width, height);

        this.wallLocation = new ArrayList<>();
        this.floorLocation = new ArrayList<>();
        this.doorLocation = new ArrayList<>();
        this.adjLocation = new ArrayList<>();
        this.cornerLocation = new ArrayList<>();

        this.getPositions();
    }

    /**
     * TODO: Remove if necessary
     * @return whether @param pos can be a door.
     * Corners cannot be doors. Doors also should not open into invalid spaces.
     */
    public boolean validDoor(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        // If it reasonably could be a door (aka there is space to generate a hallway)
        boolean inBounds = ((x - 2 > 0 && x + 2 < JonAttemptSolMain.WIDTH)
                            && (y - 2 > 0 && y + 2 < JonAttemptSolMain.HEIGHT));

        // If it is a legal spot, we check to see if it is a corner. If not, it can be a door.
        if (inBounds) {
            return !cornerLocation.contains(pos);
        }
        return false;
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

        // Generates the outside edges of the room (ignores the corners).

        // Gets the top and bottom edges.
        for (int i = 0; i < this.width; i++) {
            Position upper = new Position(this.lowerLeft, i, this.height);
            if (inBounds(upper)) {
                this.adjLocation.add(upper);
            }
            Position lower = new Position(this.lowerLeft, i, -1);
            if (inBounds(lower)) {
                this.adjLocation.add(lower);
            }
        }

        // Gets the left and right edges.
        for (int i = 0; i < this.height; i++) {
            Position left = new Position(this.lowerLeft, -1, i);
            if (inBounds(left)) {
                this.adjLocation.add(left);
            }
            Position right = new Position(this.lowerLeft, this.width, i);
            if (inBounds(right)) {
                this.adjLocation.add(right);
            }
        }
    }

    /**
     * @return whether the point (@param x, @param y) could be considered a corner.
     */
    private boolean isCorner(int x, int y) {
        if ((y == 0 && (x == 0 || x == this.width - 1))
                || (y == this.height - 1 && (x == 0 || x == this.width - 1))) {
            return true;
        }
        return false;
    }

    /**
     * @return whether the point (@param x, @param y) could be considered a wall.
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
     * TODO: Remove.
     * @Return the list of door locations @param doorLocation.
     */
    public List<Position> getDoorLocation() {
        return doorLocation;
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
     * TODO: Remove.
     * Adds a door at @param pos.
     */
    public void addDoor(Position pos) {
        doorLocation.add(pos);
    }

    /**
     * Removes @param pos from the wall locations.
     */
    public void removeWall(Position pos) {
        wallLocation.remove(pos);
    }

    /**
     * TODO: Could this be removed?
     * @Return whether position @param i is in bounds.
     */
    public boolean inBounds(Position i) {
        int x = i.getX();
        int y = i.getY();
        if (x >= JonAttemptSolMain.WIDTH || y >= JonAttemptSolMain.HEIGHT || x <= 0 || y <= 0) {
            return false;
        }
        return true;
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
     * TODO: Remove?
     * @return if the RoomAdj contains @param p as a Door.
     */
    public boolean containsDoor(Position p) {
        return doorLocation.contains(p);
    }

    /**
     * TODO: Remove?
     * @Return a form of the RoomAdj that can be printed.
     */
    @Override
    public String toString() {
        return "Lower left: " + lowerLeft + " , Upper right: " + upperRight;
    }
}

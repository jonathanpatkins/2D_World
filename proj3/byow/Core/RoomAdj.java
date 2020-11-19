package byow.Core;

import byow.TileEngine.TETile;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomAdj {

    private Position upperLeft, upperRight, lowerLeft, lowerRight;
    private List<Position> doorLocation, wallLocation, floorLocation, cornerLocation, adjLocation;
    private int numOfDoors;
    private TETile tileWall, tileFloor;
    private int height, width;

    /**
     * General Constructor:
     *  Same as in Room, but this time keeps track of where the corners of the walls for the room
     *  are and it keeps track of edges outside the walls. Illustrated in pic below with 0
     *              00000000
     *              ________
     *            0|        |0
     *            0|        |0
     *            0|        |0
     *            0|________|0
     *              00000000
     * @param lowerLeft the initial position we will build the room from
     * @param r the random object made from the seed
     * @param tileFloor the type of tile
     * @param tileWall the type of tile
     *
     * @source This class was generally inspired by Hexagon.java from the TA's
     *         implementation of Lab12.
     */
    public RoomAdj(Position lowerLeft, Random r, TETile tileFloor, TETile tileWall) {

        this.lowerLeft = lowerLeft;
        this.tileFloor = tileFloor;
        this.tileWall = tileWall;

        // Generate width (x) and height (y)
        this.width = RandomUtils.uniform(r, 3, 15);
        this.height = RandomUtils.uniform(r, 3, 15);

        // Generate upperLeft, upperRight, lowerRight from length and width
        this.upperLeft = new Position(lowerLeft, 0, height);
        this.lowerRight = new Position(lowerLeft, width, 0);
        this.upperRight = new Position(lowerLeft, width, height);

        // Generate the positions of the walls and floor
        this.wallLocation = new ArrayList<>();
        this.floorLocation = new ArrayList<>();
        this.doorLocation = new ArrayList<>();
        this.adjLocation = new ArrayList<>();
        this.cornerLocation = new ArrayList<>();

        this.getPositions();
    }

    /**
     * @return whether @param pos can be a door.
     * Corners cannot be doors. Doors also should not open into invalid spaces.
     */
    public boolean validDoor(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        // If it reasonably could be a door (aka there is space to generate a hallway)
        boolean inBounds = ((x - 2 > 0 && x + 2 < MainAdjRooms.WIDTH) ||
                (y - 2 > 0 && y + 2 < MainAdjRooms.HEIGHT));

        // If it is a legal spot, we check to see if it is a corner. If not, it can be a door.
        if (inBounds) {
            return !cornerLocation.contains(pos);
        }
        return false;
    }

    /**
     * For a given room, records the positions of the outside edges, walls, and floor
     */
    private void getPositions() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Position pos = new Position(this.lowerLeft, j, i);
                // if top, bottom, left, or right row, it is a wall.
                if (isWall(j, i)) {
                    // if it is a corner add to corner list
                    if (isCorner(j, i)) {
                        this.cornerLocation.add(pos);
                    }
                    this.wallLocation.add(pos);
                }
                // if it isn't a wall, it is a floor
                else {
                    this.floorLocation.add(pos);
                }
            }
        }


        // generates the outside edges of the room (ignores the corners)
        // Helpful for good door generation.
        // gets the top and the bottom edges
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
        // gets the left and right edges
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
        if ( (y == 0 && (x == 0 || x == this.width - 1) ) ||
                (y == this.height - 1 && (x == 0 || x == this.width - 1)) ) {
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

    public TETile getTileFloor() {
        return tileFloor;
    }

    public TETile getTileWall() {
        return tileWall;
    }

    public List<Position> getDoorLocation() {
        return doorLocation;
    }

    public List<Position> getFloorLocation() {
        return floorLocation;
    }

    public List<Position> getWallLocation() {
        return wallLocation;
    }

    public List<Position> getAdjLocation() {
        return adjLocation;
    }

    public List<Position> getCornerLocation() {
        return cornerLocation;
    }

    /**
     * Adds a door at @param pos.
     */
    public void addDoor(Position pos) {
        doorLocation.add(pos);
    }

    /**
     * Removes @param pos from the wall locations.
     * This is done if a door is to be there instead.
     */
    public void removeWall(Position pos) {
        wallLocation.remove(pos);
    }

    public boolean inBounds(Position i) {
        int x = i.getX();
        int y = i.getY();
        if (x >= MainAdjRooms.WIDTH || y >= MainAdjRooms.HEIGHT || x <= 0 || y <= 0) {
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
     * @return if the RoomAdj contains @param p as a Door.
     */
    public boolean containsDoor(Position p) {
        return doorLocation.contains(p);
    }
}

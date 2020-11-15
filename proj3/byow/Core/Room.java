package byow.Core;

import byow.TileEngine.TETile;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private Position upperLeft, upperRight, lowerLeft, lowerRight;
    private List<Position> doorLocation, wallLocation, floorLocation;
    private int numOfDoors;
    private TETile tileWall, tileFloor;
    private int length, width;

    /**
     * General Constructor:
     *  Builds a room by treating the lowerLeft position as the bottom left corner
     *  of a room and then it spreads up and right from that.
     *  Ex:
     *          width
     *         ________
     *        |        |
     * Length |        |
     *        |        |
     *        |________|
     * (start)^
     *
     * Todo:
     *  Add doors:
     *   - I was thinking that to do this first shuffle the wallLocation list
     *     and then iterate through it and test and see if a wall location would
     *     be suitable to be a door (i.e. has enough space for a hallway and the
     *     wall location is not a corner of the room)
     *  Not sure how the process should be for building the next door after the first.
     *   Things to keep in mind with this:
     *    - Need to account for the fact that I am currently building rooms based on the
     *      loc of the lowerLeft corner and then going up and right
     *    - What happens if the "next" room that we generate intersects an existing room
     *      or does not fit in our canvas space - do we keep on generating new "next" rooms
     *      until one succeeds?
     *
     * @param lowerLeft the initial position we will build the room from
     * @param r the random object made from the seed
     * @param tileFloor the type of tile
     * @param tileWall the type of tile
     */
    public Room(Position lowerLeft, Random r, TETile tileFloor, TETile tileWall) {

        this.lowerLeft = lowerLeft;
        this.tileFloor = tileFloor;
        this.tileWall = tileWall;

        // Generate length and width
        this.length = RandomUtils.uniform(r, 3, 15);
        this.width = RandomUtils.uniform(r, 3, 15);

        // Generate upperLeft, upperRight, lowerRight from length and width
        this.upperLeft = new Position(lowerLeft, length, 0);
        this.lowerRight = new Position(lowerLeft, 0, width);
        this.upperRight = new Position(lowerLeft, length, width);

        // Generate number of doors
        this.numOfDoors = RandomUtils.uniform(r, 1, 3);

        // Generate the positions of the walls and floor
        this.wallLocation = new ArrayList<>();
        this.floorLocation = new ArrayList<>();
        this.doorLocation = new ArrayList<>();
        this.getPositions();



    }

    /**
     * Adds to the wallLocation list and floorLocation list the position of all walls and floors for the room
     */
    private void getPositions() {

        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.width; j++) {

                // if top or bottom of the room add wall
                if (i == 0 || i == this.length - 1) {
                    Position wall = new Position(this.lowerLeft, j, i);
                    this.wallLocation.add(wall);
                }

                // if the sides of the room add wall
                else if (i > 0 && i < this.length - 1 && (j == 0 || j == width - 1)){
                    Position wall = new Position(this.lowerLeft, j, i);
                    this.wallLocation.add(wall);
                }

                // everything else can be considered floor
                else {
                    Position floor = new Position(this.lowerLeft, j, i);
                    this.floorLocation.add(floor);
                }
            }
        }
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
}

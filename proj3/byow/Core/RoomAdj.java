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
    private int length, width;

    /**
     * General Constructor:
     *  Same as in Room, but this time keeps track of where the corners of the walls for the room
     *  are and it keeps track of blocks outside the walls. Illustrated in pic below with 0
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
     */
    public RoomAdj(Position lowerLeft, Random r, TETile tileFloor, TETile tileWall) {

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
                    if ((i == 0 && j == 0) || (i == 0 && j == this.width - 1)
                            || (i == this.length - 1 && j == 0)
                            || (i == this.length - 1 && j == this.width - 1)) {
                        Position corner = new Position(this.lowerLeft, j, i);
                        this.cornerLocation.add(corner);
                    }
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
        Position outside = new Position(this.lowerLeft, -1, -1);
        int outsideL = this.length + 2;
        int outsideW = this.width + 2;
        for (int i = 0; i < outsideL; i++) {
            for (int j = 0; j < outsideW; j++) {
                Position adj = new Position(outside, j, i);
                // if top or bottom of the room add wall
                if (i == 0 || i == outsideL - 1) {
                    if ((i == 0 && j == 0) || (i == 0 && j == outsideW - 1)
                            || (i == outsideL - 1 && j == 0)
                            || (i == outsideL - 1 && j == outsideW - 1)) {
                        // do nothing
                    } else {
                        this.adjLocation.add(adj);
                    }
                }

                // if the sides of the room add wall
                else if (i > 0 && i < outsideL - 1 && (j == 0 || j == outsideW - 1)){
                    this.adjLocation.add(adj);
                }

                // everything else can be considered floor
                else {
                    this.adjLocation.add(adj);
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

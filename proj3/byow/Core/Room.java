package byow.Core;

import byow.TileEngine.TETile;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Room {
    private Position upperLeft, upperRight, lowerLeft, lowerRight;
    private List<Position> doorLocation;
    private int numOfDoors;
    private TETile tile;
    private int length, width;

    /**
     * General Constructor: Given starting position in space and rest is figured out later.
     *  Ideas:
     *   - Pass in a boolean value that determines if the generated room will be a hallway.
     *   What needs to be addressed:
     *   - How will be delegate the seed value to the random generator (when/where)
     *   - In the spec it says that the "floor" tiles need to be different than the tile
     *     type of the "walls" and the space that is considered "nothing"
     *       - Need some way to track the tile type for a specific Position
     * @param lowerLeft
     */
    public Room(Position lowerLeft, String seed, TETile tile) {

        this.lowerLeft = lowerLeft;
        this.tile = tile;

        // Use the seed value to give length and width

        // Get the seed num
        String subString = seed.substring(1, seed.length() - 2);
        int seedNum = Integer.parseInt(subString);

        // Create random generator
        Random random = new Random(seedNum);

        // Generate length and width
        this.length = RandomUtils.uniform(random, 3, 15);
        this.width = RandomUtils.uniform(random, 3, 15);

        // Generate upperLeft, upperRight, lowerRight from length and width
        this.upperLeft = new Position(lowerLeft, length, 0);
        this.lowerRight = new Position(lowerLeft, 0, width);
        this.upperRight = new Position(lowerLeft, length, width);

        // Generate number of doors
        // this.numOfDoors = RandomUtils.uniform(random, 1, 3);


    }

    /**
     * Returns a list Position objects for each part of the wall
     * @return
     */
    public List<Position> getPositions() {
        List<Position> result = new ArrayList<>();
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.width; j++) {
                if (i == 0 || i == this.length - 1) {
                    Position wall = new Position(this.lowerLeft, j, i);
                    result.add(wall);
                } else if (i > 0 && i < this.length - 1 && (j == 0 || j == width - 1)){
                    Position wall = new Position(this.lowerLeft, j, i);
                    result.add(wall);
                } else {
                    // do nothing
                }
            }
        }
        return result;
    }

    /**
     * Gets the tile type
     * @return
     */
    public TETile getTile() {
        return tile;
    }
}

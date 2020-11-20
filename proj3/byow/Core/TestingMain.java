package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;
// DELETE BEFORE SUBMISSION
public class TestingMain {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // Test
        String testSeed = "N23423490297423S";
        TETile testTypeWall = Tileset.WALL;
        TETile testTypeFloor = Tileset.FLOOR;

        // Create random obj to be passed around
        // Get the seed num
        // Switched to long to allow the high # of seeds they want
        String subString = testSeed.substring(1, testSeed.length() - 1);
        long seedNum = Long.parseLong(subString);

        // Create random generator
        Random random = new Random(seedNum);
        int numRooms = random.nextInt(5);
        System.out.println("Num Rooms: " + numRooms);
        for (int i = 0; i < numRooms; i += 1) {
            int xPos = random.nextInt(WIDTH);
            int yPos = random.nextInt(HEIGHT);
            Position testPos = new Position(xPos, yPos);
            Room testRoom = new Room(testPos, random, testTypeFloor, testTypeWall);
            addRoom(testRoom, world);
        }



        // draws the world to the screen
        ter.renderFrame(world);
    }

    public static void addRoom(Room r, TETile[][] world) {
        List<Position> wallPositions = r.getWallLocation();
        for (Position p: wallPositions) {
            world[p.getX()][p.getY()] = r.getTileWall();
        }
        List<Position> floorPositions = r.getFloorLocation();
        for (Position p: floorPositions) {
            world[p.getX()][p.getY()] = r.getTileFloor();
        }
    }

}
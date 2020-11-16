package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainAdjRooms {

    /**
     * Same as in Main but this time creates multiple rooms
     * Also has the methods inBounds which returns true if a room is within the bounds of our space
     * and method notInterstecting which returns true if a room does not intersect with another room.
     *
     * Todo:
     *  have a correct adj array and then for the times taht it triggers - make it turn into a diff tiel that is very vis on the display
     */


    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;

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
        List<RoomAdj> rooms = new ArrayList<>();

        String testSeed = "N232S";
        TETile testTypeWall = Tileset.WALL;
        TETile testTypeFloor = Tileset.FLOOR;

        // Create random obj to be passed around
        // Get the seed num
        String subString = testSeed.substring(1, testSeed.length() - 2);
        int seedNum = Integer.parseInt(subString);

        // Create random generator
        Random random = new Random(seedNum);

        // Generates numOfRoomsDesired into the space
        int numOfRoomsDesired = 50;
        int counter = 0;
        while (counter < numOfRoomsDesired) {
            int x = RandomUtils.uniform(random, 0, 60);
            int y = RandomUtils.uniform(random, 0, 40);
            Position testPos = new Position(x, y);

            RoomAdj testRoom = new RoomAdj(testPos, random, testTypeFloor, testTypeWall);

            if (inBounds(testRoom) && notInterstecting(testRoom, world)) {
                addRoom(testRoom, world);
                rooms.add(testRoom);
                counter += 1;
            }
        }

//        // this was here to verify that I was selecting the correct edges for each room
//        for (RoomAdj a : rooms) {
//            List<Position> temp = a.getAdjLocation();
//            for (Position i : temp) {
//                int x = i.getX();
//                int y = i.getY();
//                if (x >= WIDTH || y >= HEIGHT) {
//                       // do nothing
//                } else {
//                    world[i.getX()][i.getY()] = Tileset.FLOWER;
//                }
//            }
//        }


        // draws the world to the screen
        ter.renderFrame(world);
    }

    /**
     * Adds a room to our space
     * @param r
     * @param world
     */
    public static void addRoom(RoomAdj r, TETile[][] world) {
        List<Position> wallPositions = r.getWallLocation();
        for (Position p: wallPositions) {
            world[p.getX()][p.getY()] = r.getTileWall();
        }
        List<Position> floorPositions = r.getFloorLocation();
        for (Position p: floorPositions) {
            world[p.getX()][p.getY()] = r.getTileFloor();
        }
    }

    /**
     * Returns whether the room is in bounds of our space
     * @param temp
     * @return
     */
    // I think we could have a private helper that checks if a given Position is inside the space
    public static boolean inBounds(RoomAdj temp) {
        List<Position> wall = temp.getWallLocation();
        for (Position i : wall) {
            int x = i.getX();
            int y = i.getY();
            if (x >= WIDTH || y >= HEIGHT) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the room does not intersect any of the other created rooms.
     * @param temp
     * @param world
     * @return
     */
    public static boolean notInterstecting(RoomAdj temp, TETile[][] world) {
        for (Position i : temp.getWallLocation()) {
            if (world[i.getX()][i.getY()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }

}

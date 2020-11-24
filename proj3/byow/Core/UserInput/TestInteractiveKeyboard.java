package byow.Core.UserInput;

import byow.Core.Engine;

import byow.Core.Utils.UnionFind;
import byow.Core.Utils.Position;
import byow.Core.Utils.RandomUtils;
import byow.Core.WorldComponents.Hallway;
import byow.Core.WorldComponents.Room;
import edu.princeton.cs.introcs.StdDraw;
import byow.TileEngine.TETile;
import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  This generates a world according to a random seed
 *  It should place an avatar in the world (shown by the @ sign) in a valid location - on a floor tile in one
 *  of the rooms or halls. With WASD as keyboard input the avatar should be able to move around the world, but cannot
 *  pass though walls
 */
public class TestInteractiveKeyboard {


    public static void main(String[] args) {


        // initialize the tile rendering engine with a window of size Engine.WIDTH x Engine.HEIGHT
        TERenderer ter = new TERenderer();
        StartWindow startWindow = new StartWindow(ter);

        // initialize tiles
        TETile[][] world = new TETile[Engine.WIDTH][Engine.HEIGHT];
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // Test
        List<Room> rooms = new ArrayList<>();

        // test seed
        String seed = startWindow.start();
        String testSeed = seed;
        TETile testTypeWall = Tileset.WALL;
        TETile testTypeFloor = Tileset.FLOOR;

        // Create random obj to be passed around
        // Get the seed num
        String subString = testSeed.substring(1, testSeed.length() - 1);
        long seedNum = Long.parseLong(subString);

        // Create random generator
        Random random = new Random(seedNum);

        // Generates numOfRoomsDesired into the space
        int numOfRoomsDesired = RandomUtils.uniform(random, 6, 7);
        int counter = 0;
        int fails = 0;
        UnionFind u = new UnionFind();

        // If it fails to generate a room 30 times in a row, it stops trying.
        while (fails < 30 && counter < numOfRoomsDesired) {
            int x = RandomUtils.uniform(random, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(random, 0, Engine.HEIGHT);
            Position testPos = new Position(x, y);

            Room testRoom = new Room(testPos, random, testTypeFloor, testTypeWall);

            if (inBounds(testRoom) && notIntersecting(testRoom, world)) {
                addRoom(testRoom, world, u);
                rooms.add(testRoom);
                counter += 1;
                fails = 0;
            } else {
                fails += 1;
            }
        }

        // makes a hallway class object and then goes to connect all the rooms
        Hallway h = new Hallway(world, rooms, u, random);
        h.connectAllRooms();

        // Starting position of the avatar in a valid location
        Position currPos = generateStartingPos(world, random);

        // create input source and draw first frame - before the avatar has moved
        InputSource inputSource = new KeyboardInputSource();
        drawFrame(ter, world, currPos);

        // change frame due to keyboard input
        while (inputSource.possibleNextInput()) {
            Position nextPos = null;
            char c = inputSource.getNextKey();
            if (c == 'W') {
                nextPos = new Position(currPos, 0 , 1);
            } else if (c == 'A') {
                nextPos = new Position(currPos, -1 , 0);
            } else if (c == 'S') {
                nextPos = new Position(currPos, 0, -1);
            } else if (c == 'D') {
                nextPos = new Position(currPos, 1 , 0);
            }
            if (nextPos != null && Engine.inBounds(nextPos) && isFloor(nextPos, world)) {
                currPos = nextPos;
                drawFrame(ter, world, currPos);
            }


        }
    }

    private static Position generateStartingPos(TETile[][] world, Random r) {
        while (true) {
            int x = RandomUtils.uniform(r, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(r, 0, Engine.HEIGHT);
            Position temp = new Position(x, y);
            if (isFloor(temp, world)) {
                return temp;
            }
        }
    }

    /**
     * Adds a room (@param r) to our space (@param world).
     */
    public static void addRoom(Room r, TETile[][] world, UnionFind u) {
        u.addComponent(r);
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
    public static boolean inBounds(Room temp) {
        List<Position> wall = temp.getWallLocation();
        for (Position i : wall) {
            int x = i.getX();
            int y = i.getY();
            if (x >= Engine.WIDTH || y >= Engine.HEIGHT) {
                return false;
            }
        }
        return true;
    }

    /**
     * @Return whether the @param room does not intersect any of the other rooms in @param world.
     * Also ensures that a room would not be spawning and blocking a hallway from generating.
     */
    public static boolean notIntersecting(Room room, TETile[][] world) {
        for (Position i : room.getWallLocation()) {
            if (world[i.getX()][i.getY()] != Tileset.NOTHING) {
                return false;
            }
        }
        for (Position i: room.getFloorLocation()) {
            if (world[i.getX()][i.getY()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }


    /**
     * @Return the type of tile at Position @param i in the world.
     */
    private static TETile getWorldTile(Position i, TETile[][] world) {
        return world[i.getX()][i.getY()];
    }

    /**
     * Draws the world state given a new position of the avatar @param i
     * @param ter
     * @param world
     * @param i
     */
    public static void drawFrame(TERenderer ter, TETile[][] world, Position i) {

        // edu.princeton.cs.introcs.StdDraw
        if (Engine.inBounds(i)) {
            StdDraw.clear();
            TETile orgTile = world[i.getX()][i.getY()];
            world[i.getX()][i.getY()] = Tileset.AVATAR;
            ter.renderFrame(world);
            world[i.getX()][i.getY()] = orgTile;
        }
    }

    /**
     * Returns if the @param nextPos is a floor tile
     * @param nextPos
     * @param world
     * @return
     */
    private static boolean isFloor(Position nextPos, TETile[][] world) {
        return getWorldTile(nextPos, world) == Tileset.FLOOR;
    }


}

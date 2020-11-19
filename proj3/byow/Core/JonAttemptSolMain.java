package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class JonAttemptSolMain {


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
        List<Position> doors = new ArrayList<>();
        HashMap<Position, RoomAdj> doorMap = new HashMap<>();

        String testSeed = "N232S";
        TETile testTypeWall = Tileset.WALL;
        TETile testTypeFloor = Tileset.FLOOR;

        // Create random obj to be passed around
        // Get the seed num
        String subString = testSeed.substring(1, testSeed.length() - 1);
        long seedNum = Long.parseLong(subString);

        // Create random generator
        Random random = new Random(seedNum);

        // Generates numOfRoomsDesired into the space
        int numOfRoomsDesired = RandomUtils.uniform(random, 20, 40);
        int counter = 0;
        int fails = 0;
        UnionFind u = new UnionFind();

        // If it fails to generate a room 20 times in a row, it stops trying.
        while (fails < 20 && counter < numOfRoomsDesired) {
            int x = RandomUtils.uniform(random, 0, WIDTH);
            int y = RandomUtils.uniform(random, 0, HEIGHT);
            Position testPos = new Position(x, y);

            RoomAdj testRoom = new RoomAdj(testPos, random, testTypeFloor, testTypeWall);

            if (inBounds(testRoom) && notIntersecting(testRoom, world)) {
                addRoom(testRoom, world, u);
                rooms.add(testRoom);
                counter += 1;
                fails = 0;
            } else {
                fails += 1;
            }
        }
        for (RoomAdj i : rooms) {
            u.addComponent(i);
        }
        Hallway3 h = new Hallway3(world, rooms, u);
        h.connectAllRooms();




        // draws the world to the screen
        ter.renderFrame(world);
    }

    /**
     * Adds a room (@param r) to our space (@param world).
     */
    public static void addRoom(RoomAdj r, TETile[][] world, UnionFind u) {
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
     * @Return whether the @param room does not intersect any of the other rooms in @param world.
     * Also ensures that a room would not be spawning and blocking a hallway from generating.
     */
    public static boolean notIntersecting(RoomAdj room, TETile[][] world) {
        for (Position i : room.getWallLocation()) {
            if (world[i.getX()][i.getY()] != Tileset.NOTHING) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return whether all @param rooms are connected by @param uf.
     * The allConnected wasn't working in UnionFind so I made one here.
     */
    public static boolean checkAllConnected(List<RoomAdj> rooms, UnionFind uf) {
        RoomAdj first = rooms.get(0);
        for (int i = 1; i < rooms.size(); i += 1) {
            if (!uf.isConnected(first, rooms.get(i))) {
                return false;
            }
        }
        return true;
    }
}

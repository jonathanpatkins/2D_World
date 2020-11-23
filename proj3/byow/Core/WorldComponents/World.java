package byow.Core.WorldComponents;

import byow.Core.Engine;
import byow.Core.TileEngine.TERenderer;
import byow.Core.TileEngine.TETile;
import byow.Core.TileEngine.Tileset;
import byow.Core.UserInput.Interact;
import byow.Core.Utils.LoadWorld;
import byow.Core.Utils.Position;
import byow.Core.Utils.RandomUtils;
import byow.Core.Utils.UnionFind;
import byow.Core.WorldComponents.Hallway;
import byow.Core.WorldComponents.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class that randomly generates worlds in an intelligent and surprisingly intuitive
 * fashion based on the seed from the inputted String.
 * @author Jonathan Atkins, Jake Webster 11/20/20 - though there were many predecessors
 * to this simple-looking class that were far from simple.
 */
public class World implements java.io.Serializable {

    /**
     * @param world: A 2D array representing the TETiles at each part of the world.
     * @param testTypeWall: The type of TETile the walls of the world will be.
     * @param testTypeFloor: The type of TETile the floors of the world will be.
     * @param rooms: A list of all the rooms in the world.
     * @param seedString: The inputted string used to derive the seed.
     * @param seed: The actual seed used to ensure random generation is repeatable.
     * @param random: The Random object used for random generation.
     */
    private TETile[][] world;
    private List<Room> rooms;
    private String seedString;
    private long seed;
    private Random random;
    private TERenderer ter;
    private Position avatar;

    public World(TERenderer ter, TETile[][] world, Random random, Position avatar) {
        this.ter = ter;
        this.world = world;
        this.random = random;
        this.avatar = avatar;
        interact(avatar, null);
    }

    /**
     * Uses @param seedInput to set up the Random object's seed.
     * Prepares generation of the world based on the dimensions of the Engine.
     * Instantiates instance variables.
     */
    public World(String seedInput, TERenderer ter) {
        /**
         * This is where we will parse things
         * they could recieve N*S****
         * or N***S
         * or L:Q
         * or L****
         * or L****:Q
         * or L
         */
        char first = seedInput.charAt(0);
        if (first == 'L') {
            LoadWorld l = new LoadWorld();
            this.ter = l.getTer();
            this.random = l.getRandom();
            this.world = l.getWorld();
            this.avatar = l.getAvatar();
            interact(avatar, seedInput);
        } else {
            this.world = new TETile[Engine.WIDTH][Engine.HEIGHT];
            this.ter = ter;

            for (int x = 0; x < Engine.WIDTH; x += 1) {
                for (int y = 0; y < Engine.HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }

            this.rooms = new ArrayList<>();
            this.seedString = seedInput;



            String substring = seedString.substring(1, seedString.length());
            char[] seedArray = substring.toCharArray();
            String numString = "";
            for (char c : seedArray) {
                if (c == 'S') {
                    break;
                } else {
                    numString += c;
                }
            }



            this.seed = Long.parseLong(numString);
            this.random = new Random(seed);

            this.world = generateWorld();
            this.world = interact(null, seedInput);
        }
    }

    /**
     * Using the random seed @param seed derived in the constructor, generate a world
     * given certain constraints.
     *      1. There are a random amount of rooms that are random in size and location.
     *      2. All of these rooms are connected by hallways.
     *      3. If the algorithm cannot easily find a new place to generate a room, it stops
     *         trying, regardless of how many rooms are desired.
     *      4. Running the same seed multiple times will generate the same world.
     * @Return the world that was generated using the function's algorithm.
     */
    public TETile[][] generateWorld() {

        // Generates numOfRoomsDesired into the space
        int numOfRoomsDesired = RandomUtils.uniform(random, 20, 40);
        int counter = 0;
        int fails = 0;
        UnionFind u = new UnionFind();

        // If it fails to generate a room 30 times in a row, it stops trying.
        while (fails < 30 && counter < numOfRoomsDesired) {
            int x = RandomUtils.uniform(random, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(random, 0, Engine.HEIGHT);
            Position testPos = new Position(x, y);

            Room testRoom = new Room(testPos, random, Tileset.FLOOR, Tileset.WALL);

            if (inBounds(testRoom) && notIntersecting(testRoom, world)) {
                addRoom(testRoom, world, u);
                rooms.add(testRoom);
                counter += 1;
                fails = 0;
            } else {
                fails += 1;
            }
        }

        Hallway h = new Hallway(world, rooms, u, random);
        h.connectAllRooms();



        return world;
    }


    public TETile[][] interact(Position avatar, String userInput) {
        Interact interact = new Interact(ter, world, random, avatar, userInput);
        return world;
    }



    /**
     * Adds a Room @param r to our space (@param world) and our UnionFind @param u.
     */
    private static void addRoom(Room r, TETile[][] world, UnionFind u) {
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
     * @Return whether the @param temp is in bounds of our space
     */
    private static boolean inBounds(Room temp) {
        List<Position> wall = temp.getWallLocation();
        for (Position i : wall) {
            if (!Engine.inBounds(i)) {
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

    public TETile[][] getWorld() {
        return world;
    }
}

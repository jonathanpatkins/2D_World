package byow.Core.WorldComponents;

import byow.Core.Engine;
import byow.Core.Utils.*;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.UserInput.Interact;

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
     * @param wallType: The type of TETile the walls of the world will be.
     * @param floorType: The type of TETile the floors of the world will be.
     * @param rooms: A list of all the rooms in the world.
     * @param seedString: The inputted string used to derive the seed.
     * @param seed: The actual seed used to ensure random generation is repeatable.
     * @param random: The Random object used for random generation.
     */
    protected TETile[][] world;
    private TETile wallType, floorType;
    private ArrayList<Position> enemies;
    private List<Room> rooms;
    private String seedString;
    private long seed;
    private Random random;
    private TERenderer ter;
    private Position avatar, power, heart;
    private double theme;
    private int lives;
    private boolean powered, boosted, togglePaths;
    private ArrayList<Object> objects;

    public World(ArrayList<Object> loadedObjects) {
        this.ter = (TERenderer) loadedObjects.get(0);
        this.world = (TETile[][]) loadedObjects.get(1);
        this.avatar = (Position) loadedObjects.get(2);
        this.random = (Random) loadedObjects.get(3);
        this.floorType = (TETile) loadedObjects.get(4);
        this.wallType = (TETile) loadedObjects.get(5);
        this.enemies = (ArrayList<Position>) loadedObjects.get(6);
        this.power = (Position) loadedObjects.get(7);
        this.heart = (Position) loadedObjects.get(8);
        this.lives = (int) loadedObjects.get(9);
        this.powered = (boolean) loadedObjects.get(10);
        this.boosted = (boolean) loadedObjects.get(11);
        this.togglePaths = (boolean) loadedObjects.get(12);
        objects = loadedObjects;
        interact("L");
    }
    /**
     * Uses @param seedInput to set up the Random object's seed.
     * Prepares generation of the world based on the dimensions of the Engine.
     * Instantiates instance variables.
     */
    public World(String seedInput, TERenderer ter) {
        /**
         * This is where we will parse things
         * they could receive N*S****
         * or N***S
         * or L:Q
         * or L****
         * or L****:Q
         * or L
         */
        char first = seedInput.charAt(0);
        if (first == 'L') {
            LoadWorld l = new LoadWorld();
            this.objects = l.getObjects();
            interact(seedInput);
        } else {
            this.world = new TETile[Engine.WIDTH][Engine.HEIGHT];
            this.ter = ter;

            for (int x = 0; x < Engine.WIDTH; x += 1) {
                for (int y = 0; y < Engine.HEIGHT; y += 1) {
                    world[x][y] = Tileset.NOTHING;
                }
            }

            this.rooms = new ArrayList<>();
            this.seedString = seedInput.toUpperCase();

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
            this.theme = random.nextDouble();
            setTypes();

            this.lives = 3;
            this.powered = false;
            this.boosted = false;
            this.togglePaths = false;
            this.enemies = new ArrayList<>();
            this.world = generateWorld();
            this.objects =  new ArrayList<>();
            objects.add(this.ter);
            objects.add(this.world);
            objects.add(this.avatar);
            objects.add(this.random);
            objects.add(this.floorType);
            objects.add(this.wallType);
            objects.add(this.enemies);
            objects.add(this.power);
            objects.add(this.heart);
            objects.add(this.lives);
            objects.add(this.powered);
            objects.add(this.boosted);
            objects.add(this.togglePaths);

            this.world = interact(seedString);

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

            Room testRoom = new Room(testPos, random, floorType, wallType);

            if (inBounds(testRoom) && notIntersecting(testRoom, world)) {
                addRoom(testRoom, world, u);
                rooms.add(testRoom);
                counter += 1;
                fails = 0;
            } else {
                fails += 1;
            }
        }

        Hallway h = new Hallway(world, rooms, u, random, floorType, wallType);
        h.connectAllRooms();

        //Generates 3 enemies and the power tile.
        generateEnemies(3);
        this.power = generatePowers(Tileset.POWER);
        this.heart = generatePowers(Tileset.HEART);

        return world;
    }


    public TETile[][] interact(String userInput) {
        Interact interact = new Interact(objects, userInput);
        this.world = interact.getWorld();
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

    /**
     * Sets @param wallType and @param floorType based on @param theme.
     * 0-0.25 = Mountain Theme, 0.25-0.5 = Forest Theme, 0.5-0.75 = Beach, 0.75-1 = House Theme.
     */
    private void setTypes() {
        if (theme <= 0.25) {
            floorType = Tileset.GRASS;
            wallType = Tileset.MOUNTAIN;
        } else if (theme <= 0.5) {
            floorType = Tileset.FLOWER;
            wallType = Tileset.TREE;
        } else if (theme <= 0.75) {
            floorType = Tileset.WATER;
            wallType = Tileset.SAND;
        } else {
            floorType = Tileset.FLOOR;
            wallType = Tileset.WALL;
        }
    }

    /**
     * Generate @param num enemies to random floor tiles.
     */
    private void generateEnemies(int num) {
        for (int i = 0; i < num; i += 1) {
            boolean valid = false;
            while (!valid) {
                int x = RandomUtils.uniform(random, 0, Engine.WIDTH);
                int y = RandomUtils.uniform(random, 0, Engine.HEIGHT);
                Position enemyPos = new Position(x, y);
                if (Engine.inBounds(enemyPos) && world[x][y] == floorType) {
                    world[x][y] = Tileset.ENEMY;
                    enemies.add(enemyPos);
                    valid = true;
                }
            }
        }
    }

    /**
     * Randomly place and @return the Position of the Power up.
     */
    private Position generatePowers(TETile type) {
        while (true) {
            int x = RandomUtils.uniform(random, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(random, 0, Engine.HEIGHT);
            Position powerPos = new Position(x, y);
            if (Engine.inBounds(powerPos) && world[x][y] == floorType) {
                world[x][y] = type;
                return powerPos;
            }
        }
    }
}

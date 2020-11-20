package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class MainAdjRooms {

    // test

    /**
     * Same as in Main but this time creates multiple rooms
     * Also has the methods inBounds which returns true if a room is within the bounds of our space
     * and method not Intersecting which returns true if a room does not intersect with another room.
     *
     * Todo:
     *  have a correct adj array and then for the times that it triggers - make it turn into a diff
     *  tile that is very visible on the display
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
        List<Position> doors = new ArrayList<>();
        HashMap<RoomAdj, List<Position>> doorMap = new HashMap<>();
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
        int numOfRoomsDesired = 2;//RandomUtils.uniform(random, 20, 40);
        int counter = 0;
        int fails = 0;

        // If it fails to generate a room 20 times in a row, it stops trying.
        while (fails < 20 && counter < numOfRoomsDesired) {
            int x = RandomUtils.uniform(random, 0, WIDTH);
            int y = RandomUtils.uniform(random, 0, HEIGHT);
            Position testPos = new Position(x, y);

            RoomAdj testRoom = new RoomAdj(testPos, random, testTypeFloor, testTypeWall);
            generateDoors(testRoom, world, random);

            if (inBounds(testRoom) && notIntersecting(testRoom, world)) {
                addRoom(testRoom, world);
                rooms.add(testRoom);
                List<Position> doorLocs = testRoom.getDoorLocation();
                doors.addAll(doorLocs);
                doorMap.put(testRoom, doorLocs);
                counter += 1;
                fails = 0;
            } else {
                fails += 1;
            }
        }

        /*
            The procedure for finding nearestDoors is a little byzantine. It generally works like this.
                - uf is used to make the initial connections between rooms
                - initialMatches is used to make pairs of doors that are close to each other
                - Until all rooms are connected, we find close connections between doors
                - After this is done, we begin to use tunnels to find only the tunnels necessary
                  to connect the whole map (with the help of finalUf to verify connections)
            TODO:
                  a) Mess around with hallway generation to see how well this holds up
         */
        UnionFind finalUf = new UnionFind();
        HashMap<Position, Position> initialMatches = new HashMap<>();
        List<Pair<Pair<RoomAdj, RoomAdj>, Integer>> distances = new ArrayList<>();

        //Setting up the UnionFind
        for (int i = 0; i < rooms.size(); i += 1) {
            RoomAdj room = rooms.get(i);
            finalUf.addComponent(room);
        }

        // Done to get the closest (generally) doors connected.
        for (int i = 0; i < doors.size(); i += 1) {
            closest(rooms, doors, initialMatches, distances, doors.get(i));
        }

        // Adding only the essential connections to get everything connected.
        HashMap<Position, Position> tunnels = new HashMap<>();
        System.out.println("Initial closest doors");
        for (Position d: initialMatches.keySet()) {
            Position other = initialMatches.get(d);
            int dist = d.distance(other);
            System.out.println("D1: " + d + " D2:" + other + " Dist: " + dist);
            RoomAdj thisRoom = getRoom(d, rooms);
            RoomAdj otherRoom = getRoom(other, rooms);

            //If rooms aren't connected, then we add that tunnel and connect.
            if (!finalUf.isConnected(thisRoom, otherRoom)) {
                tunnels.put(d, other);
                finalUf.connect(thisRoom, otherRoom);
            } else {
                for (int i = 0; i < distances.size(); i += 1) {
                    Pair<RoomAdj, RoomAdj> curr = distances.get(i).getKey();
                    int currDist = distances.get(i).getValue();
                    RoomAdj first = curr.getKey();
                    RoomAdj second = curr.getValue();
                    boolean firstPair = thisRoom == first && otherRoom == second;
                    boolean secondPair = otherRoom == first && thisRoom == second;

                    // If there is a shorter distance found between already connected rooms,
                    // add that to the tunnel.
                    // TODO: Sometimes the no-longer correct tunnels are not removed from tunnel.
                    if ((firstPair || secondPair) && (dist < currDist)) {
                        distances.set(i, new Pair<>(curr, dist));
                        tunnels.put(d, other);
                    }
                }
            }
        }

        //Printing the necessary tunnels to connect all and generating hallways
        Hallway2 h = new Hallway2(world, rooms, finalUf);
        System.out.println("\nFinal state: Generate hallways with these");
        for (Position d: tunnels.keySet()) {
            Position other = tunnels.get(d);
            int horiz = d.horizontalDistance(other);
            int vert = d.verticalDistance(other);
            int direction = getDirection(horiz, vert);
            //HallwayObj newHall = h.makeCurvedHall(d, Math.abs(vert), Math.abs(horiz), direction);
            //addHall(newHall, world);
            System.out.println("D1: " + d + " D2:" + other + " Dist: " + d.distance(other));
        }
        //HallwayObj newHall = h.makeCurvedHall(new Position(23, 25), 8, 28, 2);
        //addHall(newHall, world);

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
        System.out.println("All connected: " + checkAllConnected(rooms, finalUf));

        // draws the world to the screen
        ter.renderFrame(world);
    }

    /**
     * Adds a room (@param r) to our space (@param world).
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
        List<Position> doorPositions = r.getDoorLocation();
        for (Position p: doorPositions) {
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
        // Done so that there won't be doors opening into walls.
        // (That would occur when a room was placed after doors were generated for another).
        for (Position i: room.getAdjLocation()) {
            int doorSide = getDoorSide(room, i);
            if (!validLocation(i, world, doorSide)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates doors for @param room.
     *      Picks random walls (not corners) to be doors.
     *      If a spot is not a valid location, it is removed from potential spots.
     *      If there are no spots left, it gives up on generating.
     *      Valid locations are defined by RoomAdj.validDoor(Position) and by not
     *      being in a position that makes generating hallways impossible.
     *
     * Uses @param random to generate a random number of doors.
     * Uses @param world to check if surrounding tiles are already filled (thus ineligible).
     * @source for copying values from an ArrayList without pointing to the same list:
     *      https://stackoverflow.com/questions/6536094/java-arraylist-copy.
     */
    private static void generateDoors(RoomAdj room, TETile[][] world, Random random) {
        int numOfDoors = RandomUtils.uniform(random, 1, 3);
        for (int i = 0; i < numOfDoors; i += 1) {
            boolean valid = false;
            List<Position> options = new ArrayList<>(room.getWallLocation());
            while( !valid && options.size() > 0) {
                int doorIndex = random.nextInt(options.size());
                Position door = options.get(doorIndex);
                int doorSide = getDoorSide(room, door);
                if (room.validDoor(door) && validLocation(door, world, doorSide)) {
                    room.addDoor(door);
                    room.removeWall(door);
                    valid = true;
                }
                options.remove(door);
            }
        }
    }

    /**
     * Takes Position @param pos from @param room and looks in the direction of @param doorSide.
     * @return if it can be a valid Location (ie nothing there in @param world).
     * Example:
     *      Position(12, 10) with doorSide 0 (so we are looking up top).
     *      If world[12][11] already has a wall there, we shouldn't generate a door.
     *      If there is nothing at world[12][11], we could generate a door and still
     *      have be able to make a hallway of some kind.
     */
    private static boolean validLocation(Position pos, TETile[][] world, int doorSide) {
        int x = pos.getX();
        int y = pos.getY();
        if (x <= 0 || x + 1 >= WIDTH || y <= 0 || y + 1 >= HEIGHT) {
            return false;
        }
        if (doorSide == 0) {
            return world[x][y + 1] == Tileset.NOTHING;
        } else if (doorSide == 1) {
            return world[x + 1][y] == Tileset.NOTHING;
        } else if (doorSide == 2) {
            return world[x][y - 1] == Tileset.NOTHING;
        } else {
            return world[x - 1][y] == Tileset.NOTHING;
        }
    }

    /**
     * Given Position @param door, @return whether it is on the left, right, top, or bottom of
     * Room @room. 0 = top, 1 = right, 2 = bottom, 3 = left (clockwise).
     */
    private static int getDoorSide(RoomAdj room, Position door) {
        Position lowLeft = room.getLowerLeft();
        Position upRight = room.getUpperRight();
        int right = upRight.getX();
        int bottom = lowLeft.getY();
        int top = upRight.getY();
        if (door.getY() == top) {
            return 0;
        } else if (door.getX() == right) {
            return 1;
        } else if (door.getY() == bottom) {
            return 2;
        } else { //we know if it isn't any of those it has to be the left side.
            return 3;
        }
    }

    /**
     * Finds the closest door to @param p based on certain constraints.
     *      1. They cannot share the same RoomAdj
     *      2. Their @param rooms cannot already be connected in @param uf
     * @param rooms is used to ensure points don't compare to ones from the same RoomAdj.
     * @param doorsLeft is the list of doors essentially.
     * @param initialMatches is the HashMap for storing closest Position pairs.
     */
    public static void closest(List<RoomAdj> rooms, List<Position> doorsLeft, HashMap<Position, Position> initialMatches,
                               List<Pair<Pair<RoomAdj, RoomAdj>, Integer>> distances, Position p) {
        List<Position> doorCopy = new ArrayList<>(doorsLeft);
        RoomAdj myRoom = getRoom(p, rooms);

        // Remove doors from the same room from consideration.
        for (Position d: doorsLeft) {
            if (myRoom.containsDoor(d)) {
                doorCopy.remove(d);
            }
        }

        //while (doorCopy.size() > 0) {
        for (int i = 0; i < doorCopy.size(); i+= 1) {
            Position near = p.nearest(doorCopy);
            int dist = p.distance(near);
            RoomAdj nearRoom = getRoom(near, rooms);
            // If rooms aren't connected then this is a good pair. TODO remove
            Pair<RoomAdj, RoomAdj> connection = new Pair<>(myRoom, nearRoom);
            Pair<Pair<RoomAdj, RoomAdj>, Integer> connectDist = new Pair<>(connection, dist);

            distances.add(connectDist);
            initialMatches.put(p, near);
        }
    }

    /**
     * @return the room from @param rooms that @param p belongs to, or null if None.
     */
    public static RoomAdj getRoom(Position p, List<RoomAdj> rooms) {
        for (RoomAdj r : rooms) {
            if (r.containsDoor(p)) {
                return r;
            }
        }
        return null;
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

    /**
     * Adds a HallwayObj to the world
     * @param r the HallwayObj
     * @param world the world
     */
    public static void addHall(HallwayObj r, TETile[][] world) {
        if (r != null) {
            List<Position> wallPositions = r.getWall();
            for (Position p : wallPositions) {
                world[p.getX()][p.getY()] = Tileset.WALL;
            }
            List<Position> floorPositions = r.getFloor();
            for (Position p : floorPositions) {
                world[p.getX()][p.getY()] = Tileset.FLOOR;
            }
        }
    }

    /**
     * @return the direction the hallway should go given @param horiz and @param vert
     * signifying the horizontal and vertical distance between the doors.
     *  NOTE: curvedHallway does have 8 different orientation options, but I believe that
     *  we really only need the first 4- for #s 5-8, we can just input the other Position
     *  as our starting point and accomplish the same goal.
     */
    public static int getDirection(int horiz, int vert) {
        if (horiz > 0) {
            if (vert > 0) {
                return 1;
            } else {
                return 3;
            }
        } else {
            if (vert > 0) {
                return 2;
            } else {
                return 4;
            }
        }
    }
}

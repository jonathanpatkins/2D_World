package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

/**
 * A more intelligent Hallway that finds optimal places to generate Hallways.
 * @author Johnathan Atkins, Jake Webster 11/18/20.
 */
public class Hallway2 {
    /**
     * @param world: The grid that everything is generated on.
     * @param rooms: The List of RoomAdjs that exist in @param world.
     * @param halls: The list of HallwayObjs that exist in @param world.
     * @param obj: The UnionFind that is used to ensure a connected structure.
     */
    private TETile[][] world;
    private List<RoomAdj> rooms;
    private List<HallwayObj> halls;
    private UnionFind obj;

    /**
     * @param world sets the world array of this class.
     * @param rooms sets the rooms list of this class.
     * @param obj sets the UnionFind obj of this class.
     * Also initializes @param halls: There are no Hallways before we start generation.
     */
    public Hallway2(TETile[][] world, List<RoomAdj> rooms, UnionFind obj) {
        this.world = world;
        this.rooms = rooms;
        this.obj = obj;
        this.halls = new ArrayList<>();
    }

    /**
     * Makes function calls to connect all the rooms.
     */
    public void connectAllRooms() {
        for (RoomAdj i: rooms) {
            for (RoomAdj j: rooms) {
                if (i != j) {
                    connectRoomsStraight(i, j);
                }
            }
        }
        //TODO: Remove once we have confidence in our solution
        if (!obj.allConnected()) {
            System.out.println("not connected");
        } else {
            System.out.println("connected");
        }
    }

    /**
     * Begins the process of connecting rooms @param i and @param j.
     */
    private void connectRoomsStraight(RoomAdj i, RoomAdj j) {
        if (!obj.isConnected(i, j)) {
            HallwayObj temp = null;
            List<Position> closeWalls = closestWalls(i, j);
            // a is the start position, and b is the end position.
            Position a = closeWalls.get(0);
            Position b = closeWalls.get(1);

            int direction = checkDirection(a);
            int x = Math.abs(b.getX() - a.getX());
            int y = Math.abs(b.getY() - a.getY());

            if (x == 0) {
                if (direction == 1) {
                    temp = makeVerticalHall(a, y + 1, true);
                } else {
                    temp = makeVerticalHall(a, y + 1, false);
                }
            } else if (y == 0) {
                if (direction == 2) {
                    temp = makeHorizontalHall(a, x + 1, true);
                } else {
                    temp = makeHorizontalHall(a, x + 1, false);
                }
            }
            addHall(temp, world);
        }
    }

    /**
     * @Return the direction for @param a.
     * Direction is defined by where you could best generate a Hallway from-
     * so you wouldn't want to build inwards towards a floor for instance.
     *
     * Directions: 0 = anything else (should not occur), 1 = up, 2 = right,
     * 3 = down, 4 = left.
     */
    private int checkDirection(Position a) {
        // If it hits nothing or wall - so basically it can't hit the floor.
        Position up = new Position(a, 0, 1);
        Position right = new Position(a, 1, 0);
        Position down = new Position(a, 0, -1);
        Position left = new Position(a, -1, 0);

        if (inBounds(up)) {
            if (world[up.getX()][up.getY()] != Tileset.FLOOR) {
                return 1;
            }
        }
        if (inBounds(right)) {
            if (world[right.getX()][right.getY()] != Tileset.FLOOR) {
                return 2;
            }
        }
        if (inBounds(down)) {
            if (world[down.getX()][down.getY()] != Tileset.FLOOR) {
                return 3;
            }
        }
        if (inBounds(left)) {
            if (world[left.getX()][left.getY()] != Tileset.FLOOR) {
                return 4;
            }
        }
        return 0;
    }

    /**
     * @Return a list containing the closest Positions on the walls of RoomAdj @param a
     * and RoomAdj @param b. Corners are excluded.
     */
    private List<Position> closestWalls(RoomAdj a, RoomAdj b) {
        List<Position> returnV = new ArrayList<>();

        List<Position> aWalls = a.getWallLocation();
        List<Position> bWalls = b.getWallLocation();

        List<Position> aCorner = a.getCornerLocation();
        List<Position> bCorner = b.getCornerLocation();

        Position bestAWall = aWalls.get(0);
        Position bestBWall = bWalls.get(0);
        double bestD = bestAWall.distance(bestBWall);

        for (Position i: aWalls) {
            if (!aCorner.contains(i)) {
                for (Position j: bWalls) {
                    if (!bCorner.contains(j)) {
                        double contender = i.distance(j);
                        if (contender < bestD) {
                            bestD = contender;
                            bestAWall = i;
                            bestBWall = j;
                        }
                    }
                }
            }
        }
        returnV.add(bestAWall);
        returnV.add(bestBWall);
        return returnV;
    }


    /**
     * Makes a vertical hallway.
     * If it hits a wall before it reaches its desired length, still create the wall,
     * thus merging the Hallway with another Hallway or a Room, as long as the wall
     * is not a corner. If the hallway is built to its desired length, then it is a
     * dead end hallway.
     *
     * Starts the Hallway of @param length from Position @param p.
     * Orientation is determined by @param up: true = up, false = down.
     * @Return the HallwayObj generated in the process.
     */
    private HallwayObj makeVerticalHall(Position p, int length, boolean up) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();

        for (int j = 0; j < length; j++) {
            int i;
            if (up) {
                i = j;
            } else {
                i = -j;
            }

            Position nextP = new Position(p, 0, i);
            Position nextWall1 = new Position(nextP, -1, 0);
            Position nextWall2 = new Position(nextP, 1, 0);

            floor.add(nextP);
            wall.add(nextWall1);
            wall.add(nextWall2);

            if (!inBounds(nextP) || !inBounds(nextWall1) || !inBounds(nextWall2)) {
                return null;
            }

            // Stop generating the Hallway if a corner is hit.
            if (up) {
                if (isCorner(p, world, 1) && length != 2) {
                    return null;
                }
            } else {
                if (isCorner(p, world, 2) && length != 2) {
                    return null;
                }
            }

            // You cannot put a Hallway on a floor- stop generation.
            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR
                    || world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
                return null;
            }


        }

        Position endPoint = floor.get(floor.size() - 1);
        if (world[endPoint.getX()][endPoint.getY()] == Tileset.NOTHING) {
            floor.remove(floor.size() - 1);
            wall.add(endPoint);
        }
        HallwayObj hall = new HallwayObj(floor, wall, length, 3);
        return hall;
    }

    /**
     * Makes a horizontal hallway.
     * If it hits a wall before it reaches its desired length, still create the wall,
     * thus merging the Hallway with another Hallway or a Room, as long as the wall
     * is not a corner. If the hallway is built to its desired length, then it is a
     * dead end hallway.
     *
     * Starts the Hallway of @param length from Position @param p.
     * Orientation is determined by @param up: true = up, false = down.
     * @Return the HallwayObj generated in the process.
     */
    private HallwayObj makeHorizontalHall(Position p, int width, boolean right) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();

        for (int j = 0; j < width; j++) {
            int i;
            if (right) {
                i = j;
            } else {
                i = -j;
            }

            Position nextP = new Position(p, i, 0);
            Position nextWall1 = new Position(nextP, 0, -1);
            Position nextWall2 = new Position(nextP, 0, 1);
            floor.add(nextP);
            wall.add(nextWall1);
            wall.add(nextWall2);

            if (!inBounds(nextP) || !inBounds(nextWall1) || !inBounds(nextWall2)) {
                return null;
            }

            // Stop generating the Hallway if a corner is hit.
            if (right) {
                if (isCorner(p, world, 3) && width != 2) {
                    return null;
                }
            } else {
                if (isCorner(p, world, 4)  && width != 2) {
                    return null;
                }
            }

            // You cannot put a Hallway on a floor- stop generation.
            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR
                    || world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
                return null;
            }
        }

        Position endPoint = floor.get(floor.size() - 1);
        if (world[endPoint.getX()][endPoint.getY()] == Tileset.NOTHING) {
            floor.remove(floor.size() - 1);
            wall.add(endPoint);
        }
        HallwayObj hall = new HallwayObj(floor, wall, 3, width);
        return hall;
    }

    /**
     * @Return whether Position @param p is a corner looking within grid @param world.
     * @param direction is used to look in the appropriate direction for the Position
     *                  to make the determination.
     */
    private boolean isCorner(Position p, TETile[][] world, int direction) {
        Position nextPos;
        if (direction == 1) {
            nextPos = new Position(p, 0, 1);
        } else if (direction == 2) {
            nextPos = new Position(p, 0, 1);
        } else if (direction == 3) {
            nextPos = new Position(p, 1, 0);
        } else {
            nextPos = new Position(p, -1, 0);
        }
        return inBounds(nextPos) && world[p.getX()][p.getY()] == Tileset.WALL
                && world[nextPos.getX()][nextPos.getY()] == Tileset.WALL;
    }

    private void addHall(HallwayObj r, TETile[][] world) {
        if (r != null && r.getWall().size() < 30) {
            obj.addComponent(r);
            halls.add(r);
            List<Position> wallPositions = r.getWall();
            for (Position p : wallPositions) {
                if (world[p.getX()][p.getY()] == Tileset.FLOOR) {
                    world[p.getX()][p.getY()] = Tileset.FLOOR;
                } else {
                    world[p.getX()][p.getY()] = Tileset.WALL;
                }
            }
            List<Position> floorPositions = r.getFloor();
            for (Position p: floorPositions) {
                /*
                 * Could make this even better:
                 *  Everytime you encounter a wall when placing a floor tile
                 *   You first call a whatIsThisPart of method to see what component it is
                 *   Then connect the two things and go on your merry way
                 *   TODO: Remove this comment when we figure stuff out.
                 */
                if (world[p.getX()][p.getY()] == Tileset.WALL) {
                    Object temp = whichComponent(p);
                    obj.connect(temp, r);
                }
                world[p.getX()][p.getY()] = Tileset.FLOOR;
            }
        }
    }

    /**
     * @Return the type that the component that contains @param p is.
     */
    private Object whichComponent(Position p) {
        for (RoomAdj i: rooms) {
            for (Position wall: i.getWallLocation()) {
                if (p.equals(wall)) {
                    return i;
                }
            }
        }
        for (HallwayObj i: halls) {
            for (Position wall: i.getWall()) {
                if (p.equals(wall)) {
                    return i;
                }
            }
        }
        return null;
    }

    /**
     * @Return whether @param p is in bounds in the world.
     * TODO: Maybe we make only one of these in Main and use that instead.
     *      Also, change the JonAttemptSolMain.WIDTH/HEIGHT to the class we
     *      define stuff in at the end. (probably Engine)
     */
    private boolean inBounds(Position p) {
        int x = p.getX();
        int y = p.getY();

        if (x >= JonAttemptSolMain.WIDTH || x < 0) {
            return false;
        }
        if (y >= JonAttemptSolMain.HEIGHT || y < 0) {
            return false;
        }
        return true;
    }
}

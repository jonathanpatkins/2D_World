package byow.Core.WorldComponents;

import byow.Core.*;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.Core.Utils.Position;
import byow.Core.Utils.RandomUtils;
import byow.Core.Utils.UnionFind;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A more intelligent Hallway that finds optimal places to generate Hallways.
 * @author Jonathan Atkins, Jake Webster 11/18/20.
 */
public class Hallway  implements java.io.Serializable {
    /**
     * @param world: The grid that everything is generated on.
     * @param rooms: The List of RoomAdjs that exist in @param world.
     * @param halls: The list of HallwayObjs that exist in @param world.
     * @param obj: The UnionFind that is used to ensure a connected structure.
     * @param random: The random used for generation.
     * @param floorType: The TETile to use for the Hallway's floors.
     * @param wallType: The TETile to use for the Hallway's walls.
     */
    private final boolean VERTICAL = true;
    private final boolean HORIZONTAL = false;
    private TETile[][] world;
    private List<Room> rooms;
    private List<HallwayObj> halls;
    private UnionFind obj;
    private Random random;
    private TETile floorType, wallType;

    /**
     * @param w sets the world array of this class.
     * @param rms sets the rooms list of this class.
     * @param uf sets the UnionFind obj of this class.
     * @param r sets the Random of this class.
     * @param f sets the floorType of this class.
     * @param w sets the wallType of this class.
     * Also initializes @param halls: There are no Hallways before we start generation.
     */
    public Hallway(TETile[][] wld, List<Room> rms, UnionFind uf, Random r, TETile f, TETile w) {
        this.world = wld;
        this.rooms = rms;
        this.obj = uf;
        this.halls = new ArrayList<>();
        this.random = r;
        this.floorType = f;
        this.wallType = w;
    }

    /**
     * Makes function calls to connect all the rooms.
     */
    public void connectAllRooms() {
        for (Room i: rooms) {
            for (Room j: rooms) {
                // If not already connected, go to connect.
                if (!obj.isConnected(i, j)) {
                    connectRoom(i, j);
                }
            }
        }
    }

    /**
     * Begins the process of connecting rooms @param i and @param j.
     * Attempts connecting 20 times; if it does not, then they cannot connect.
     */
    private void connectRoom(Room i, Room j) {
        for (int fails = 0; fails < 20; fails += 1) {
            Position a = findLoc(i);
            Position b = findLoc(j);
            if (connectPoint(a, b)) {
                break;
            }
        }
    }

    /**
     * @Returns the Position of @param i, if it is not a corner.
     */
    private Position findLoc(Room i) {
        Position returnV = null;
        while (returnV == null) {
            int x = RandomUtils.uniform(random, 0, i.getWallLocation().size() - 1);
            Position temp = i.getWallLocation().get(x);

            if (!i.getCornerLocation().contains(temp)) {
                returnV = temp;
            }
        }
        return returnV;
    }

    /**
     * Figure out the distance between @param a and @param b starting from @param a.
     * Then, use the width and length to work on generating a Hallway that connects them.
     * @Return whether a Hallway can be generated between the two.
     */
    private boolean connectPoint(Position a, Position b) {
        int widthOrg = Math.abs(b.getX() - a.getX());
        int width = Math.abs(widthOrg) + 1;

        int lengthOrg = Math.abs(b.getY() - a.getY());
        int length = Math.abs(lengthOrg) + 1;

        int a1;
        int a2;
        // Try vertical and then horizontal generation from a.
        HallwayObj firstPart1;
        if (a.getY() > b.getY()) {
            firstPart1 = makeHallway(a, -length, VERTICAL);
            a1 = -lengthOrg;
        } else {
            firstPart1 = makeHallway(a, length, VERTICAL);
            a1 = lengthOrg;
        }
        HallwayObj secondPart1;
        if (a.getX() > b.getX()) {
            secondPart1 = makeHallway(b, width, HORIZONTAL);
        } else {
            secondPart1 = makeHallway(b, -width, HORIZONTAL);
        }

        // Then, try horizontal and then vertical generation from a.
        HallwayObj firstPart2;
        if (a.getX() > b.getX()) {
            firstPart2 = makeHallway(a, -width, HORIZONTAL);
            a2 = -widthOrg;
        } else {
            firstPart2 = makeHallway(a, width, HORIZONTAL);
            a2 = widthOrg;
        }
        HallwayObj secondPart2;
        if (a.getY() > b.getY()) {
            secondPart2 = makeHallway(b, length, VERTICAL);

        } else {
            secondPart2 = makeHallway(b, -length, VERTICAL);
        }

        if ((firstPart1 == null || secondPart1 == null)
                && (firstPart2 == null || secondPart2 == null)) {
            return false;
        } else if (firstPart1 == null || secondPart1 == null) {
            addHall(firstPart2, world);
            addHall(secondPart2, world);
            Position intersection = new Position(a, a2, 0);
            addCorner(intersection);
            return true;
        } else if (firstPart2 == null || secondPart2 == null) {
            addHall(firstPart1, world);
            addHall(secondPart1, world);
            Position intersection = new Position(a, 0, a1);
            addCorner(intersection);
            return true;
        }
        return false;
    }

    /**
     * Add a corner at @param p in the most reasonable location.
     */
    private void addCorner(Position p) {
        Position upperRight = new Position(p, 1, 1);
        Position upperLeft = new Position(p, -1, 1);
        Position lowerRight = new Position(p, 1, -1);
        Position lowerLeft = new Position(p, -1, -1);
        if (getWorldTile(upperRight) == Tileset.NOTHING) {
            world[upperRight.getX()][upperRight.getY()] = wallType;
        } else if (getWorldTile(upperLeft) == Tileset.NOTHING) {
            world[upperLeft.getX()][upperLeft.getY()] = wallType;
        } else if (getWorldTile(lowerRight) == Tileset.NOTHING) {
            world[lowerRight.getX()][lowerRight.getY()] = wallType;
        } else if (getWorldTile(lowerLeft) == Tileset.NOTHING) {
            world[lowerLeft.getX()][lowerLeft.getY()] = wallType;
        }
    }




    /**
     * @param p: The Position that is being considered.
     * @param distance: The desired distance to place. It should be > 0.
     * @param vertical: Whether a vertical Hallway is being generated or not.
     * @return whether the next Position the Hallway is going to is already a floor.
     */
    private boolean secondPlacedFloorIsFloor(Position p, int distance, boolean vertical) {
        Position nextPos;
        if (vertical) {
            if (distance > 0) {
                nextPos = new Position(p, 0, 1);
            } else {
                nextPos = new Position(p, 0, -1);
            }
        } else {
            if (distance > 0) {
                nextPos = new Position(p, 1, 0);
            } else {
                nextPos = new Position(p, -1, 0);
            }
        }
        if (Engine.inBounds(nextPos)) {
            return getWorldTile(nextPos) == floorType;
        }
        return false;
    }


    /**
     * Makes a hallway, vertical if @param vertical is true and horizontal if vertical is false.
     * If it hits a wall before it reaches its desired length, still create the wall,
     * thus merging the Hallway with another Hallway or a Room, as long as the wall
     * is not a corner. If the hallway is built to its desired length, then it is a
     * dead end hallway.
     *
     * Starts the Hallway of @param length from Position @param p.
     * Orientation is determined by @param up: true = up, false = down.
     * @Return the HallwayObj generated in the process.
     */
    public HallwayObj makeHallway(Position p, int length, boolean vertical) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();

        int absLength = Math.abs(length);
        for (int j = 0; j < absLength; j++) {
            int i;
            if (length > 0) {
                i = j;
            } else {
                i = -j;
            }

            Position pos;
            Position wall1;
            Position wall2;
            if (vertical) {
                pos = new Position(p, 0, i);
                wall1 = new Position(pos, -1, 0);
                wall2 = new Position(pos, 1, 0);
            } else {
                pos = new Position(p, i, 0);
                wall1 = new Position(pos, 0, -1);
                wall2 = new Position(pos, 0, 1);
            }

            // If the position is not in bounds, a Hallway cannot be generated.
            if (!Engine.inBounds(pos) || !Engine.inBounds(wall1) || !Engine.inBounds(wall2)) {
                return null;
            }

            // Generation should also fail if the Hallway is going to hit a corner.
            if (isCorner(pos, length, vertical)) {
                return null;
            }

            // If the second tile that you put down for floor hits floor, this happens
            // in the instance you are beginning at the position of a wall tile, and then
            // the second tile is inside some component; the first tile should then be a
            // wall tile.
            if (j == 0 && secondPlacedFloorIsFloor(pos, length, vertical)) {
                wall.add(pos);
            } else {
                floor.add(pos);
            }
            wall.add(wall1);
            wall.add(wall2);
        }

        HallwayObj hall;
        if (vertical) {
            hall = new HallwayObj(floor, wall, absLength, 3);
        } else {
            hall = new HallwayObj(floor, wall, 3, absLength);
        }
        return hall;
    }


    /**
     * @Return whether Position @param p is a corner looking within the world.
     * @param distance represents how much more Hallway is left; nothing happens if <=0.
     * @param vertical is true if this is a vertical Hallway, and false if it is horizontal.
     */
    private boolean isCorner(Position p, int distance, boolean vertical) {
        Position nextPos;
        Position nextNextPos;
        if (vertical) {
            if (distance > 0) {
                nextPos = new Position(p, 0, 1);
                nextNextPos = new Position(p, 0, 2);
            } else {
                nextPos = new Position(p, 0, -1);
                nextNextPos = new Position(p, 0, -2);
            }
        } else {
            if (distance > 0) {
                nextPos = new Position(p, 1, 0);
                nextNextPos = new Position(p, 2, 0);
            } else {
                nextPos = new Position(p, -1, 0);
                nextNextPos = new Position(p, -2, 0);
            }
        }
        boolean flag = Engine.inBounds(nextPos) && getWorldTile(p) == wallType
                        && getWorldTile(nextPos) == wallType;
        if (flag && Engine.inBounds(nextNextPos) && getWorldTile(nextNextPos) == floorType) {
            return false;
        } else {
            return flag;
        }
    }

    /**
     * Adds HallwayObj @param h to @param wrld.
     */
    public void addHall(HallwayObj h, TETile[][] wrld) {
        if (h != null && h.getWall().size() > 0) {
            obj.addComponent(h);
            halls.add(h);

            List<Position> wallPositions = h.getWall();
            List<Position> floorPositions = h.getFloor();

            for (Position p: wallPositions) {
                if (getWorldTile(p) == floorType) {
                    wrld[p.getX()][p.getY()] = floorType;
                } else {
                    wrld[p.getX()][p.getY()] = wallType;
                }
            }

            for (Position p: floorPositions) {
                // When you place a floor on a wall that is a connection from the Hallway
                // to the other component.
                if (getWorldTile(p) != Tileset.NOTHING) {
                    Object temp = whichComponent(p);
                    obj.connect(temp, h);
                }
                wrld[p.getX()][p.getY()] = floorType;
            }
        }
    }

    /**
     * @Return the type that the component that contains @param p is, or
     * null if it is none of them.
     */
    private Object whichComponent(Position p) {
        for (Room i : rooms) {
            for (Position wall : i.getWallLocation()) {
                if (p.equals(wall)) {
                    return i;
                }
            }
        }
        for (Room i : rooms) {
            for (Position floor : i.getFloorLocation()) {
                if (p.equals(floor)) {
                    return i;
                }
            }
        }
        for (HallwayObj i : halls) {
            for (Position wall : i.getWall()) {
                if (p.equals(wall)) {
                    return i;
                }
            }
        }
        for (HallwayObj i : halls) {
            for (Position floor : i.getFloor()) {
                if (p.equals(floor)) {
                    return i;
                }
            }
        }
        return null;
    }

    /**
     * @Return the type of tile at Position @param i in the world.
     */
    private TETile getWorldTile(Position i) {
        return world[i.getX()][i.getY()];
    }
}

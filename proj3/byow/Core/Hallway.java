package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hallway {
    /**
     * Hallway2.java but uses the UnionFind obj
     */
    TETile[][] world;
    List<Room> rooms;
    List<HallwayObj> halls;
    UnionFind obj;
    Random random;

    public Hallway(TETile[][] world, List<Room> rooms, UnionFind obj, Random random) {
        this.world = world;
        this.rooms = rooms;
        this.obj = obj;
        this.halls = new ArrayList<>();
        this.random = random;
    }

    public void connectAllRooms() {
        for (Room i : rooms) {
            for (Room j : rooms) {

                // if not connected, go to connect
                if (!obj.isConnected(i, j)) {
                    connectRoom(i , j);
                }
            }
        }
        if (obj.allConnected()) {
            System.out.println("fuck yeah");
        }
    }

    private void connectRoom(Room i, Room j) {

        // try to connect 20 times, if fails oh well
        for (int fails = 0; fails < 20; fails += 1) {
            Position a = findLoc(i);
            Position b = findLoc(j);
            if (connectPoint(a, b)) {
                break;
            }

        }
    }
    private Position findLoc(Room i) {
        Position returnV = null;
        while (returnV == null) {
            int x = RandomUtils.uniform(random, 0, i.getWallLocation().size() - 1);
            Position temp = i.getWallLocation().get(x);

            // if not a corner
            if (!i.getCornerLocation().contains(temp)) {
                returnV = temp;
            }
        }
        return returnV;
    }
    public boolean connectPoint(Position a, Position b) {
        /**
         * Figure out the distance between the two starting from a
         *
         * then use the width and length
         *
         * if the second thing you put down hits floor then do not open up the first wall
         */
        int widthOrg = Math.abs(b.getX() - a.getX());
        int width = Math.abs(widthOrg) + 1;

        int lengthOrg = Math.abs(b.getY() - a.getY());
        int length = Math.abs(lengthOrg) + 1;

        int a1;
        int a2;
        // first try vert then horiz from a
        HallwayObj firstPart1;
        if (a.getY() > b.getY()) {
            firstPart1 = makeVerticalHall(a, -length);
            a1 = -lengthOrg;
        } else {
            firstPart1 = makeVerticalHall(a, length);
            a1 = lengthOrg;
        }
        HallwayObj secondPart1;
        if (a.getX() > b.getX()) {
            secondPart1 = makeHorizontalHall(b, width);
        } else {
            secondPart1 = makeHorizontalHall(b, -width);
        }


        // second try horiz then vert
        HallwayObj firstPart2;
        if (a.getX() > b.getX()) {
            firstPart2 = makeHorizontalHall(a, -width);
            a2 = -widthOrg;
        } else {
            firstPart2 = makeHorizontalHall(a, width);
            a2 = widthOrg;
        }
        HallwayObj secondPart2;
        if (a.getY() > b.getY()) {
            secondPart2 = makeVerticalHall(b, length);

        } else {
            secondPart2 = makeVerticalHall(b, -length);
        }

        if ((firstPart1 == null || secondPart1 == null) && (firstPart2 == null || secondPart2 == null)){
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

    private void addCorner(Position p) {
        Position upperRight = new Position(p, 1, 1);
        Position uppperLeft = new Position(p, -1, 1);
        Position lowerRight = new Position(p, 1, -1);
        Position lowerLeft = new Position(p, -1, -1);
        if (getWorldTile(upperRight) == Tileset.NOTHING) {
            world[upperRight.getX()][upperRight.getY()] = Tileset.WALL;
        } else if (getWorldTile(uppperLeft) == Tileset.NOTHING) {
            world[uppperLeft.getX()][uppperLeft.getY()] = Tileset.WALL;
        } else if (getWorldTile(lowerRight) == Tileset.NOTHING) {
            world[lowerRight.getX()][lowerRight.getY()] = Tileset.WALL;
        } else if (getWorldTile(lowerLeft) == Tileset.NOTHING) {
            world[lowerLeft.getX()][lowerLeft.getY()] = Tileset.WALL;
        }
    }







    /**
     * Makes a vertical hallway
     * If it hits a wall before it reaches its desired length, still create the wall (aka merging of hallway with
     * hallway or hallway with room) if the wall if not a corner. If the hallway is built to its desired length,
     * then its a dead end hallway
     * @param p
     * @param length
     * @return
     */
    public HallwayObj makeVerticalHall(Position p, int length) {
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

            Position pos = new Position(p, 0, i);
            Position wall1 = new Position(pos, -1, 0);
            Position wall2 = new Position(pos, 1, 0);


            // check if inbounds
            if (!inBounds(pos) || !inBounds(wall1) || !inBounds(wall2)) {
                return null;
            }

            // stop if floor hits corner
            // isCorner(pos, length, vertical?)
            if (isCorner(pos, length, true)) {
                return null;
            }

            // if the second thing you put down for floor hits floor, do not make the first floor floor, make it wall
            if (j == 0 && secondPlacedFloorIsFloor(pos, length, true)) {
                wall.add(pos);
            } else {
                floor.add(pos);
            }

            wall.add(wall1);
            wall.add(wall2);


        }

        HallwayObj hall = new HallwayObj(floor, wall, absLength, 3);
        return hall;
    }

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
        return getWorldTile(nextPos) == Tileset.FLOOR;
    }


    /**
     * Makes a horiz hallway
     * If it hits a wall before it reaches its desired length, still create the wall (aka merging of hallway with
     * hallway or hallway with room) if the wall if not a corner. If the hallway is built to its desired length,
     * then its a dead end hallway
     * @param p
     * @param width
     * @return
     */
    public HallwayObj makeHorizontalHall(Position p, int width) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();

        int absWidth = Math.abs(width);
        for (int j = 0; j < absWidth; j++) {
            int i;
            if (width > 0) {
                i = j;
            } else {
                i = -j;
            }

            Position pos = new Position(p, i, 0);
            Position wall1 = new Position(pos, 0, -1);
            Position wall2 = new Position(pos, 0, 1);

            if (!inBounds(pos) || !inBounds(wall1) || !inBounds(wall2)) {
                return null;
            }

            // stop if floor hits corner
            // isCorner(pos, length, vertical?)
            if (isCorner(pos, width, false)) {
                return null;
            }

            // if the second thing you put down for floor hits floor, do not make the first floor floor, make it wall
            if (j == 0 && secondPlacedFloorIsFloor(pos, width, false)) {
                wall.add(pos);
            } else {
                floor.add(pos);
            }

            wall.add(wall1);
            wall.add(wall2);
        }

        HallwayObj hall = new HallwayObj(floor, wall, 3, width);
        return hall;
    }



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
        boolean flag = inBounds(nextPos) && getWorldTile(p) == Tileset.WALL &&
                getWorldTile(nextPos) == Tileset.WALL;
        if (flag && inBounds(nextNextPos) && getWorldTile(nextNextPos) == Tileset.FLOOR) {
            return false;
        } else {
            return flag;
        }
    }

    private TETile getWorldTile(Position i) {
        return world[i.getX()][i.getY()];
    }

    public void addHall(HallwayObj h, TETile[][] world) {
        if (h != null && h.getWall().size() > 0) {
                obj.addComponent(h);
                halls.add(h);

            List<Position> wallPositions = h.getWall();
            List<Position> floorPositions = h.getFloor();

            for (Position p : wallPositions) {
                if (getWorldTile(p) == Tileset.FLOOR) {
                    world[p.getX()][p.getY()] = Tileset.FLOOR;
                } else {
                    world[p.getX()][p.getY()] = Tileset.WALL;
                }
            }

            for (Position p : floorPositions) {
                // when you place a floor on a wall that is a connection from the hallway
                // to the other component
                if (getWorldTile(p) != Tileset.NOTHING) {

                        Object temp = whichComponent(p);
                        obj.connect(temp, h);

                }
                world[p.getX()][p.getY()] = Tileset.FLOOR;
            }
        }

    }

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
     * General method to see if method is inbounds of the screen or not
     * @param p
     * @return
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

package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;

public class Hallway2 {
    /**
     * Hallway2.java but uses the UnionFind obj
     */
    TETile[][] world;
    List<RoomAdj> rooms;
    List<HallwayObj> halls;
    UnionFind obj;

    public Hallway2(TETile[][] world, List<RoomAdj> rooms, UnionFind obj) {
        this.world = world;
        this.rooms = rooms;
        this.obj = obj;
        this.halls = new ArrayList<>();
    }

    public void connectAllRooms() {
        for (RoomAdj i : rooms) {
            for (RoomAdj j : rooms) {
                if (i != j) {
                    connectRoomsStraight(i , j);
                }
            }
        }
        if (!obj.allConnected()) {
            List<RoomAdj> redo = new ArrayList<>();
            for (int i = 0; i < rooms.size(); i += 1) {
                int misCount = 0;
                RoomAdj curRoom = rooms.get(i);
                for (int j = 0; j < rooms.size(); j += 1) {
                    if (!obj.isConnected(curRoom, rooms.get(j))) {
                        misCount += 1;
                    }
                }
                // This will only fail if more than 3 rooms aren't connected (near impossible)
                if (misCount > 3) {
                    redo.add(rooms.get(i));
                }
            }
            for (int i = 0; i < redo.size(); i += 1) {
                System.out.println("bad " + redo.get(i));
            }
            System.out.println("fuck");
        } else {
            System.out.println("yeah");
        }
    }

    private void connectRoomsStraight(RoomAdj i, RoomAdj j) {
        if (!obj.isConnected(i, j)) {

            HallwayObj temp = null;

            List<Position> closeWalls = closestWalls(i, j); // kinda break the abstraction barrier right here
            Position a = closeWalls.get(0); // start position
            Position b = closeWalls.get(1); // end position

            int dirn = checkDirection(a);
            int x = Math.abs(b.getX() - a.getX());
            int y = Math.abs(b.getY() - a.getY());
            // x == x
            if (x == 0) {
                if (dirn == 1) {
                    temp = makeVerticalHall(a, y + 1, true);
                } else {
                    temp = makeVerticalHall(a, y + 1, false);
                }
            } else if (y == 0) {
                if (dirn == 2) {
                    temp = makeHorizontalHall(a, x + 1, true);
                } else {
                    temp = makeHorizontalHall(a, x + 1, false);
                }
            }
            addHall(temp, world);
        }
    }

    // 1 up
    // 2 right
    // 3 down
    // 4 left
    // 0 catch all for anything else
    private int checkDirection(Position a) {
        // if it hits nothing or wall - so basically cant hit floor
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

    private List<Position> closestWalls(RoomAdj a, RoomAdj b) {
        List<Position> returnV = new ArrayList<>();

        List<Position> aWalls = a.getWallLocation();
        List<Position> bWalls = b.getWallLocation();

        List<Position> aCorner = a.getCornerLocation();
        List<Position> bCorner = b.getCornerLocation();

        Position bestAWall = aWalls.get(0);
        Position bestBWall = bWalls.get(0);
        double bestD = aWalls.get(0).distance(bWalls.get(0));

        for (Position i : aWalls) {
            if (!aCorner.contains(i)) {
                for (Position j : bWalls) {
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
     * Makes a vertical hallway
     * If it hits a wall before it reaches its desired length, still create the wall (aka merging of hallway with
     * hallway or hallway with room) if the wall if not a corner. If the hallway is built to its desired length,
     * then its a dead end hallway
     * @param p
     * @param length
     * @param up
     * @return
     */
    public HallwayObj makeVerticalHall(Position p, int length, boolean up) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();

//        floor.add(p);
//        wall.add(new Position(p, -1, 0));
//        wall.add(new Position(p, 1, 0));


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

            // stop if floor hits corner
            if (up) {
                if (isCorner(p, world, 1) && length != 2) {
                    return null;
                }
            } else {
                if (isCorner(p, world, 2) && length != 2) {
                    return null;
                }
            }

            // if you try to put a wall on a floor thats a no go
            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR ||
                    world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
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
        return inBounds(nextPos) && world[p.getX()][p.getY()] == Tileset.WALL &&
                world[nextPos.getX()][nextPos.getY()] == Tileset.WALL;
    }

    /**
     * Makes a horiz hallway
     * If it hits a wall before it reaches its desired length, still create the wall (aka merging of hallway with
     * hallway or hallway with room) if the wall if not a corner. If the hallway is built to its desired length,
     * then its a dead end hallway
     * @param p
     * @param width
     * @param right
     * @return
     */
    public HallwayObj makeHorizontalHall(Position p, int width, boolean right) {
        List<Position> floor = new ArrayList<>();
        List<Position> wall = new ArrayList<>();
//        floor.add(p);
//        wall.add(new Position(p, 0, -1));
//        wall.add(new Position(p, 0, 1));

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

            // stop if floor hits corner
            if (right) {
                if (isCorner(p, world, 3) && width != 2) {
                    return null;
                }
            } else {
                if (isCorner(p, world, 4)  && width != 2) {
                    return null;
                }
            }

            // if you try to put a wall on a floor thats a no go
            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR || world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
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

    public void addHall(HallwayObj r, TETile[][] world) {
        //System.out.println(r.getWall().size());
        if (r != null) { //&& r.getWall().size() < 30) {
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
            for (Position p : floorPositions) {
                /**
                 * Could make this even better:
                 *  Everytime you encounter a wall when placing a floor tile
                 *   You first call a whatIsThisPart of method to see what component it is
                 *   Then connect the two things and go on your merry way
                 */
                if (world[p.getX()][p.getY()] == Tileset.WALL) {
                    Object temp = whichComponent(p);
                    obj.connect(temp, r);
                }
                world[p.getX()][p.getY()] = Tileset.FLOOR;



            }
        }

    }

    private Object whichComponent(Position p) {
        for (RoomAdj i : rooms) {
            for (Position wall : i.getWallLocation()) {
                if (p.equals(wall)) {
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

        if (x >= MainAdjRooms.WIDTH || x < 0) {
            return false;
        }
        if (y >= MainAdjRooms.HEIGHT || y < 0) {
            return false;
        }
        return true;
    }
}

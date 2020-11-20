package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
//DELETE BEFORE SUBMISSION
public class Hallway {
    /**
     * Todo:
     *  - Need a general method for adding hallways to the world - this must utilize the UnionFind obj
     *  - Need to add a condition to each method s.t. the method returns null if you run into a corner
     *    while building the wall - this will probably need some method that cycles through all the corner of all the
     *    rooms and hallwayObjs
     */
    TETile[][] world;
    List<RoomAdj> rooms;

    public Hallway(TETile[][] world, List<RoomAdj> rooms, UnionFind obj) {
        this.world = world;
        this.rooms = rooms;
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

        floor.add(p);
        wall.add(new Position(p, -1, 0));
        wall.add(new Position(p, 1, 0));


        for (int j = 1; j < length; j++) {
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

            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR ||
                    world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
                return null;
            }

            if (world[nextP.getX()][nextP.getY()] == Tileset.WALL) {
                // make the hallway obj
                HallwayObj hall = new HallwayObj(floor, wall, j, 3);
                return hall;
            }
        }

        Position endPoint = floor.remove(floor.size() - 1);
        wall.add(endPoint);
        HallwayObj hall = new HallwayObj(floor, wall, length, 3);
        return hall;
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

        floor.add(p);
        wall.add(new Position(p, 0, -1));
        wall.add(new Position(p, 0, 1));

        for (int j = 1; j < width; j++) {
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

            if (world[nextWall1.getX()][nextWall1.getY()] == Tileset.FLOOR ||
                    world[nextWall2.getX()][nextWall2.getY()] == Tileset.FLOOR) {
                return null;
            }

            if (world[nextP.getX()][nextP.getY()] == Tileset.WALL) {
                // make the hallway obj
                HallwayObj hall = new HallwayObj(floor, wall, 3, j);
                return hall;
            }
        }

        Position endPoint = floor.remove(floor.size() - 1);
        wall.add(endPoint);
        HallwayObj hall = new HallwayObj(floor, wall, 3, width);
        return hall;
    }

    /**
     * Same behavior as above, merge if ends early, do not make wall if hits corner, deadend if reaches desired length
     * 1 means up/right, 2 means up/left, 3 means down/right, 4 means down/left
     * 5 left/up 6 left/down 7 right/up 8 right/down
     * @param p
     * @param length must be of length at least 3
     * @param width must be of width at least 3
     * @param option
     * @return
     */
    public HallwayObj makeCurvedHall(Position p, int length, int width, int option) {
        if (option == 1) {
            HallwayObj vert = makeVerticalHall(p, length, true);
            if (vert == null) {
                return null;
            }
            if (vert.getLength() == length) {
                Position start = new Position(p, 1, vert.getLength() - 2);
                HallwayObj horiz = makeHorizontalHall(start, width - 2, true);
                if (horiz == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 2) {
            HallwayObj vert = makeVerticalHall(p, length, true);
            if (vert == null) {
                return null;
            }
            if (vert.getLength() == length) {
                Position start = new Position(p, -1, vert.getLength() - 2);
                HallwayObj horiz = makeHorizontalHall(start, width - 2, false);
                if (horiz == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 3) {
            HallwayObj vert = makeVerticalHall(p, length, false);
            if (vert == null) {
                return null;
            }
            if (vert.getLength() == length) {
                Position start = new Position(p, 1, -(vert.getLength() - 2));
                HallwayObj horiz = makeHorizontalHall(start, width - 2, true);
                if (horiz == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 4) {
            HallwayObj vert = makeVerticalHall(p, length, false);
            if (vert == null) {
                return null;
            }
            if (vert.getLength() == length) {
                Position start = new Position(p, -1, -(vert.getLength() - 2));
                HallwayObj horiz = makeHorizontalHall(start, width - 2, false);
                if (horiz == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 5) {
            HallwayObj horiz = makeHorizontalHall(p, width, false);
            if (horiz == null) {
                return null;
            }
            if (horiz.getWidth() == width) {
                Position start = new Position(p, -(horiz.getWidth() - 2), 1);
                HallwayObj vert = makeVerticalHall(start, length - 2, true);
                if (vert == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 6) {
            HallwayObj horiz = makeHorizontalHall(p, width, false);
            if (horiz == null) {
                return null;
            }
            if (horiz.getWidth() == width) {
                Position start = new Position(p, -(horiz.getWidth() - 2), -1);
                HallwayObj vert = makeVerticalHall(start, length - 2, false);
                if (vert == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 7) {
            HallwayObj horiz = makeHorizontalHall(p, width, true);
            if (horiz == null) {
                return null;
            }
            if (horiz.getWidth() == width) {
                Position start = new Position(p, horiz.getWidth() - 2, 1);
                HallwayObj vert = makeVerticalHall(start, length - 2, true);
                if (vert == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        } else if (option == 8) {
            HallwayObj horiz = makeHorizontalHall(p, width, true);
            if (horiz == null) {
                return null;
            }
            if (horiz.getWidth() == width) {
                Position start = new Position(p, horiz.getWidth() - 2, -1);
                HallwayObj vert = makeVerticalHall(start, length - 2, false);
                if (vert == null) {
                    return null;
                }
                Position corner = this.getCornerOfCurvedHall(p, vert.getLength(), horiz.getWidth(), option);
                HallwayObj curved = new HallwayObj(vert, horiz, vert.getLength(), horiz.getWidth(), corner);
                return curved;
            }
        }
        return null;
    }

    /**
     * Gets the corner for storing for later in HallwayObj creation
     * @param p
     * @param length
     * @param width
     * @param option
     * @return
     */
    private Position getCornerOfCurvedHall(Position p, int length, int width, int option) {
        if (option == 1) {
            return new Position(p, -1, length);
        } else if (option == 2) {
            return new Position(p, 1, length);
        } else if (option == 3) {
            return new Position(p, -1, -length);
        } else if (option == 4) {
            return new Position(p, 1, -length);
        } else {
            return null;
        }
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

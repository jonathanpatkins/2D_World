package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;

public class Hallway {
    /**
     * Should take in parameters the TetTile[][] world and the rooms and the UnionFind Obj
     *
     * should have Methods:
     *  make vertical hall
     *  make horiz hadd
     *      these methods should only take in a starting point and they should keep on building up until they hit
     *      another wall. If that other wall is a corner - return false or something and do not make the wall,
     *      otherwise make the wall and add it (maybe have a different class for a hall object) to the component list
     *      in UnionFind
     *
     *      Make hallways must be of size 2 or greater
     *      Todo: need to not make the hall if it goes out of bounds
     */
    TETile[][] world;
    List<RoomAdj> rooms;

    public Hallway(TETile[][] world, List<RoomAdj> rooms, UnionFind obj) {
        this.world = world;
        this.rooms = rooms;
    }

    // this will operate on the basis that the starting position is a floor tile
    // make a hallway vertically
    // have it take in a specified length
    // if it hits another wall before the length then create wall
        // if that hits a corner of the wall do not create
    // if the length runs out make the hallway
    //the hallway object should have points beginning at the first doorway
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
    // Todo: need to make alteration for thwn the length is < 3 and need to account for that one thing on ipad
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
     * Then there should be some methods for L shaped halls
     *  This reduces to making a vertical hall and then a horizontal hall or vice versa with minor adjustments
     */

    // again if you hit something, end early, if its a corner dont make it
    // if you complete the length and width then make it boxed off end
    // must be at least of length and width 3
    // 1 means up/right, 2 means up/left, 3 means down/right, 4 means down/left
    // 5 left/up 6 left/down 7 right/up 8 right/down - still need to do
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

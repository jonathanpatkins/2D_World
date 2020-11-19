package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;


public class TestHall {

    /**
     * Tests the use of hallways
     */

    private static final int WIDTH = 60;
    private static final int HEIGHT = 40;

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
        List<RoomAdj> r = new ArrayList<>();
        UnionFind x = new UnionFind();

        // make hallway obj
        Hallway h = new Hallway(world, r, x);

        // test room
        HallwayObj test = h.makeVerticalHall(new Position(25, 15), 15, true);
        //addHall(test, world);
       HallwayObj test1 = h.makeHorizontalHall(new Position(37, 25), 20, false);
       //addHall(test1, world);

       HallwayObj test2 = h.makeCurvedHall(new Position(1, 1), 20, 8, 1);
       //addHall(test2, world);


        Position pt1 = new Position(10, 10);
        Position pt2 = new Position(31, 17);
        int horiz = pt1.horizontalDistance(pt2);
        int vert = pt1.verticalDistance(pt2);
        int direction = getDirection(horiz, vert);
        HallwayObj test3 = h.makeCurvedHall(pt1, Math.abs(vert), Math.abs(horiz), direction);
        addHall(test3, world);
        // draws the world to the screen
        System.out.println("render");
        ter.renderFrame(world);
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

    //1 means up/right, 2 means up/left, 3 means down/right, 4 means down/left
    // * 5 left/up 6 left/down 7 right/up 8 right/down

    /**
     * @return the direction the hallway should go given @param horiz and @param vert
     * signifying the horizontal and vertical distance between the doors.
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

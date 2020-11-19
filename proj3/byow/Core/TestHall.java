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
        addHall(test, world);
       HallwayObj test1 = h.makeHorizontalHall(new Position(37, 25), 20, false);
       addHall(test1, world);

       HallwayObj test2 = h.makeCurvedHall(new Position(1, 1), 20, 8, 1);
       addHall(test2, world);




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

}

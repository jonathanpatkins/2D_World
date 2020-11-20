package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // Hallway h = new Hallway(world, r, x);

        // test room
//        HallwayObj test = h.makeCurvedHall(new Position(20, 20), 50, 8, 8);
//        addHall(test, world);
//        HallwayObj test1 = h.makeHorizontalHall(new Position(27, 25), 20, false);
//        addHall(test1, world);

        Hallway2 test = new Hallway2(world, r, x);
        Random ran = new Random(123);
        r.add(new RoomAdj(new Position(10, 10), ran, Tileset.FLOOR, Tileset.WALL));
        r.add(new RoomAdj(new Position(27, 33), ran, Tileset.FLOOR, Tileset.WALL));
        test.connectAllRooms();
        //HallwayObj testHall1 = test.makeVerticalHall(new Position(20, 20), 17, true);
        //HallwayObj testHall2 = test.makeHorizontalHall(new Position(27, 27), 17, false);
        //test.addHall(testHall1, world);
        //test.addHall(testHall2, world);




        // draws the world to the screen
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

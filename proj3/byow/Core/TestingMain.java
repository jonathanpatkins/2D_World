package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Hexagon;
import byow.lab12.Position;

import java.util.List;

public class TestingMain {

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
        String testSeed = "N232S";
        TETile testType = Tileset.WALL;
        Position testPos = new Position(10, 10);

        Room testRoom = new Room(testPos, testSeed, testType);

        addRoom(testRoom, world);



        // draws the world to the screen
        ter.renderFrame(world);
    }

    public static void addRoom(Room r, TETile[][] world) {
        List<Position> wallPositions = r.getPositions();
        for (Position p: wallPositions) {
            world[p.getX()][p.getY()] = r.getTile();
        }
    }

}
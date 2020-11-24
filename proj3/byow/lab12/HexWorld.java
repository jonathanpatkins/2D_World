
package byow.lab12;

import byow.Core.Utils.Position;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;

/**
 *  Draws a world that is mostly empty except for a small region.
 */
public class HexWorld {

    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;

    public static void main(String[] args) {
        Tessellation tessellation = new Tessellation(3);
        List<Hexagon> hexagons = tessellation.getHexagons();

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        for (Hexagon hex: hexagons) {
            addHexagon(hex, world);
        }
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);



        // makes the hexagon tessalation
        /*
        Hexagon hex = new Hexagon(3, new Position(20, 20), Tileset.MOUNTAIN);
        List<Position> hexPositions = hex.getPositions();
        for (Position p: hexPositions) {
            world[p.getX()][p.getY()] = hex.getTile();
        } */

        // draws the world to the screen
        ter.renderFrame(world);
    }
    public static void addHexagon(Hexagon hex, TETile[][] world) {
        List<Position> hexPositions = hex.getPositions();
        for (Position p: hexPositions) {
            world[p.getX()][p.getY()] = hex.getTile();
        }
    }


}
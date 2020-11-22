package byow.Core;

import byow.Core.TileEngine.TERenderer;
import byow.Core.TileEngine.TETile;
import byow.Core.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.Random;

public class Interact {

    // floor tile type
    TETile x;

    // now you can take input from keyboard to interact with world
    public Interact(TERenderer ter, TETile[][] world, Random random, TETile x) {
        this(ter, world, random, null, x);
    }

    // now you can take input from keyboard to interact with world
    public Interact(TERenderer ter, TETile[][] world, Random random, Position p, TETile x) {
        // Starting position of the avatar in a valid location
        this.x = x;

        Position currPos = p;
        if (p == null) {
            currPos = generateStartingPos(world, random, x);
        }


        // create input source and draw first frame - before the avatar has moved
        InputSource inputSource = new KeyboardInputSource();
        drawFrame(ter, world, currPos);

        boolean getReadyForQuit = false;

        // change frame due to keyboard input
        while (inputSource.possibleNextInput()) {
            Position nextPos = null;
            char c = inputSource.getNextKey();
            if (c == 'W') {
                nextPos = new Position(currPos, 0 , 1);
            } else if (c == 'A') {
                nextPos = new Position(currPos, -1 , 0);
            } else if (c == 'S') {
                nextPos = new Position(currPos, 0, -1);
            } else if (c == 'D') {
                nextPos = new Position(currPos, 1 , 0);
            } else if (c == ':') {
                getReadyForQuit = true;
            } else if (c == 'Q' && getReadyForQuit) {
                SaveWorld saveWorld = new SaveWorld(ter, world, currPos, random, x);
            }
            if (nextPos != null && Engine.inBounds(nextPos) && isFloor(nextPos, world, x)) {
                currPos = nextPos;
                drawFrame(ter, world, currPos);
            }


        }
    }

    /**
     * If no starting pos of the avatar is given, generate one
     * @param world
     * @param r
     * @param floor
     * @return
     */
    private static Position generateStartingPos(TETile[][] world, Random r, TETile floor) {
        while (true) {
            int x = RandomUtils.uniform(r, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(r, 0, Engine.HEIGHT);
            Position temp = new Position(x, y);
            if (isFloor(temp, world, floor)) {
                return temp;
            }
        }
    }

    /**
     * @Return the type of tile at Position @param i in the world.
     */
    private static TETile getWorldTile(Position i, TETile[][] world) {
        return world[i.getX()][i.getY()];
    }

    /**
     * Draws the world state given a new position of the avatar @param i
     * @param ter
     * @param world
     * @param i
     */
    public static void drawFrame(TERenderer ter, TETile[][] world, Position i) {

        // edu.princeton.cs.introcs.StdDraw
        if (Engine.inBounds(i)) {
            StdDraw.clear();
            TETile orgTile = world[i.getX()][i.getY()];
            world[i.getX()][i.getY()] = Tileset.AVATAR;
            ter.renderFrame(world);
            world[i.getX()][i.getY()] = orgTile;
        }
    }

    /**
     * Returns if the @param nextPos is a floor tile
     * @param nextPos
     * @param world
     * @return
     */
    private static boolean isFloor(Position nextPos, TETile[][] world, TETile x) {
        return getWorldTile(nextPos, world).equals(Tileset.FLOOR);
    }


}

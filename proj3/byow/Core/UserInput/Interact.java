package byow.Core.UserInput;

import byow.Core.Engine;
import byow.Core.Utils.Position;
import byow.Core.Utils.RandomUtils;
import byow.Core.Utils.SaveWorld;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class Interact {

    TERenderer ter;
    TETile[][] world;
    Random random;
    Position avatar;
    String userInput;
    private TETile floorType, wallType;

    // now you can take input from keyboard to interact with world
    public Interact(TERenderer ter, TETile[][] world, Random random, Position p, String userInput,
                    TETile f, TETile w) {
        // Starting position of the avatar in a valid location

        this.ter = ter;
        this.world = world;
        this.random = random;
        this.avatar = p;
        this.userInput = userInput;
        this.floorType = f;
        this.wallType = w;
        if (p == null) {
            avatar = generateStartingPos(world, random);
        }

        // returns if the program should quit or not
        if (!doUserInput(userInput)) {
            ter.initialize(Engine.WIDTH, Engine.HEIGHT);

            // create input source and draw first frame - before the avatar has moved
            InputSource inputSource = new KeyboardInputSource();
            drawFrame(ter, world, avatar);

            boolean getReadyForQuit = false;

            // change frame due to keyboard input
            while (inputSource.possibleNextInput()) {
                Position nextPos = null;
                char c = inputSource.getNextKey();
                if (c == 'W') {
                    nextPos = new Position(avatar, 0, 1);
                    makeMove(nextPos, c);
                } else if (c == 'A') {
                    nextPos = new Position(avatar, -1, 0);
                    makeMove(nextPos, c);
                } else if (c == 'S') {
                    nextPos = new Position(avatar, 0, -1);
                    makeMove(nextPos, c);
                } else if (c == 'D') {
                    nextPos = new Position(avatar, 1, 0);
                    makeMove(nextPos, c);
                } else if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    SaveWorld saveWorld = new SaveWorld(ter, world, avatar, random, floorType, wallType);
                    break;
                }
            }
        }
    }
    private void makeMove(Position next, char c) {
        if ((next != null && Engine.inBounds(next) && isFloor(next, world)) || c == '0') {
            if (c != '0') {
                avatar = next;
            }
            drawFrame(ter, world, avatar);
        }
    }
    // for now lets go off the assumption that you are passed an unparsed string
    private boolean doUserInput(String userInput) {
        boolean quit = false;
        if (userInput != null) {
            char[] charArray = userInput.toCharArray();
            Position nextPos = avatar;
            boolean getReadyForQuit = false;
            int sCounter = 1;

            if (userInput.charAt(0) == 'N') {
                sCounter = 0;
            }

            for (char c : charArray) {
                if (c == 'W') {
                    nextPos = new Position(avatar, 0, 1);
                } else if (c == 'A') {
                    nextPos = new Position(avatar, -1, 0);
                } else if (c == 'S' && sCounter > 0) {
                    nextPos = new Position(avatar, 0, -1);
                } else if (c == 'S') {
                    sCounter += 1;
                } else if (c == 'D') {
                    nextPos = new Position(avatar, 1, 0);
                } else if (c == ':') {
                    getReadyForQuit = true;
                } else if (c == 'Q' && getReadyForQuit) {
                    new SaveWorld(ter, world, avatar, random, floorType, wallType);
                    quit = true;
                }
                if (nextPos != null && Engine.inBounds(nextPos) && isFloor(nextPos, world)) {
                    avatar = nextPos;
                }
            }
        }
        return quit;
    }

    /**
     * If no starting pos of the avatar is given, generate one
     * @param world
     * @param r
     * @return
     */
    private Position generateStartingPos(TETile[][] world, Random r) {
        while (true) {
            int x = RandomUtils.uniform(r, 0, Engine.WIDTH);
            int y = RandomUtils.uniform(r, 0, Engine.HEIGHT);
            Position temp = new Position(x, y);
            if (isFloor(temp, world)) {
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
    public void drawFrame(TERenderer ter, TETile[][] world, Position i) {

        // edu.princeton.cs.introcs.StdDraw
        if (Engine.inBounds(i)) {
            StdDraw.clear();
            TETile orgTile = world[i.getX()][i.getY()];
            world[i.getX()][i.getY()] = Tileset.AVATAR;
            ter.renderFrame(world);
            world[i.getX()][i.getY()] = orgTile;

            // basic structure for hud
            // it should be this in some sort of continuous loop
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            Position nextMouse = new Position(x, y);
            if (Engine.inBounds(nextMouse)) {
                TETile mouseTile;
                if (nextMouse == i) {
                    mouseTile = Tileset.AVATAR;
                } else {
                    mouseTile = getWorldTile(nextMouse, world);
                }
                StringBuilder temp = new StringBuilder();
                temp.append(mouseTile.description());
                temp.append(" (");
                temp.append(x);
                temp.append(", ");
                temp.append(y);
                temp.append(")");
                StdDraw.setPenColor(Color.GREEN);
                StdDraw.text(4, 1, temp.toString());
                StdDraw.show();
            }
        }
    }

    /**
     * Returns if the @param nextPos is a floor tile
     * @param nextPos
     * @param world
     * @return
     */
    private boolean isFloor(Position nextPos, TETile[][] world) {
        return getWorldTile(nextPos, world) == floorType;
    }


}

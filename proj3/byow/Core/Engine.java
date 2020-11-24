package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.UserInput.StartWindow;
import byow.Core.Utils.LoadWorld;
import byow.Core.Utils.Position;
import byow.Core.WorldComponents.World;

/**
 * A class that generates a world using either Keyboard or String inputs.
 * @author Jonathan Atkins Jake Webster 11/20/20.
 */
public class Engine {
    private TERenderer ter = new TERenderer();
    private TETile[][] finalWorldFrame;
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        StartWindow startWindow = new StartWindow(ter);
        String seed = startWindow.start().toUpperCase();
        char[] seedArray = seed.toCharArray();

        /**
         * When we save the game what elements should we keep track of
         *  - the window state
         *  - the position of the avatar
         */

        for (char c : seedArray) {
            if (c == 'N') {
                interactWithInputString(seed);
                break;
            } else if (c == 'L') {
                // do something
                // fetch save
                LoadWorld loadWorld = new LoadWorld();
                loadWorld.load();
                break;
            } else if (c == ':') {
                // do something - nothing?
            } else if (c == 'Q') {
                // save before quiting

                System.exit(0);
            } else if (c == 'S') {
                // do something
            }
        }

    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        World world = new World(input.toUpperCase(), ter);
        finalWorldFrame = world.getWorld();
        // Do not render yet- merely return the finalWorldFrame.
        // ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        // ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }

    /**
     * @Return whether @param p is within the bounds of the world.
     */
    public static boolean inBounds(Position p) {
        int x = p.getX();
        int y = p.getY();

        if (x >= Engine.WIDTH || x < 0) {
            return false;
        }
        if (y >= Engine.HEIGHT || y < 0) {
            return false;
        }
        return true;
    }

    /**
     * @Return a String representation of the generated world.
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < Engine.WIDTH; i += 1) {
            for (int j = 0; j < Engine.HEIGHT; j += 1) {
                s += finalWorldFrame[i][j] + " ";
            }
            s += "\n";
        }
        return s;
    }
}

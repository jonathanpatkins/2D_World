package byow.Core.UserInput;

import byow.Core.Engine;
import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;

/**
 * Creates an interactive start window for the game.
 * @author Jonathan Atkins, Jake Webster 11/21/20.
 */
public class StartWindow {

    private TERenderer ter;
    private boolean ready;

    /**
     * Initializes the TERenderer with @param ter, which is used to generate
     * the start window for the game.
     */
    public StartWindow(TERenderer ter) {
        this.ter = ter;
        this.ready = false;
    }

    /**
     * Sets up the menu and allows for keyboard inputs.
     * @Return the seed generated using the menu - either typed by the player in the format
     * N***S or blank, indicating that another mode has been selected.
     */
    public String start() {
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);

        // Setting up canvas.
        // StdDraw.setCanvasSize(WIDTH, HEIGHT);
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.enableDoubleBuffering();

        // Draw the initial frame.
        drawCanvas("");

        // Allow for keyboard input.
        InputSource inputSource = new KeyboardInputSource();

        // These will hold our seed.
        String seed = "";
        String displaySeed = "";

        // This flag will turn true when 'N' or 'B' is inputted. This is for a new game.
        // Thus we will now begin to collect the input in the @param displaySeed so we can view.
        boolean newFlag = false;

        /**
         * Preferably in the future stuff is only displayed when you are entering the number part
         * of the seed for a new game, but for now lets just display the entire seed input
         * TODO: Analyze if this is necessary.
         * No parsing of the seed is done in here, do it all in the engine class
         */
        while (inputSource.possibleNextInput()) {
            // Get key stroke from keyboard.
            char c = inputSource.getNextKey();

            // Add the input to our seed value.
            seed += c;
            if (c == 'N') {
                newFlag = true;
                ready = true;
            } else if (c == 'L') {
                break;
            } else if (c == 'Q') {
                break;
            } else if (c == 'S') {
                break;
            } else if (c == 'B') {
                newFlag = true;
                ready = true;
            } else if (newFlag) {
                displaySeed += c;
            }
            drawCanvas(displaySeed);
        }
        // Pass the seed to some other class where it can be processed.
        return seed;
    }

    /**
     * Draw the canvas with the @param seedSoFar
     */
    private void drawCanvas(String seedSoFar) {
        // Start with clear canvas.
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.green);
        int x = Engine.WIDTH / 2;

        // Text and initial screen.
        StdDraw.text(x, 17, "(N)ew game");
        StdDraw.text(x, 16, "(L)oad");
        StdDraw.text(x, 15, "(B)uild");
        StdDraw.text(x, 14, "(Q)uit");
        if (ready) {
            StdDraw.text(x, 12, "Enter seed");
        }

        // Draw the seed we have thus far.
        StdDraw.text(x, 11, seedSoFar);
        StdDraw.show();
    }
}

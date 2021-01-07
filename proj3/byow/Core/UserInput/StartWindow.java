package byow.Core.UserInput;

import byow.Core.Engine;
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

        while (inputSource.possibleNextInput()) {

            char c = inputSource.getNextKey();

            if (seed.length() == 0) {
                if (c == 'N') {
                    ready = true;
                    seed += c;
                } else if (c == 'L') {
                    seed += c;
                    break;
                } else if (c == 'B') {
                    ready = true;
                    seed += c;
                } else if (c == 'Q') {
                    System.exit(0);
                }
            } else {
                if (c >= 48 && c <= 57) {
                    // Only numbers added to the seed - bad inputs ie 6m5 become 65.
                    seed += c;
                    displaySeed += c;
                } else if (c == 'S' && seed.length() > 1) {
                    seed += c;
                    break;
                } else if (c == 'Q') {
                    System.exit(0);
                }
            }
            drawCanvas(displaySeed);
        }
        return seed;
    }

    /**
     * Draw the canvas with the @param seedSoFar
     */
    private void drawCanvas(String seedSoFar) {
        // Start with clear canvas.
        StdDraw.clear(Color.BLACK);
        int x = Engine.WIDTH / 2;

        // Text and initial screen.
        StdDraw.setPenColor(Color.white);
        StdDraw.text(x, 25, "BYOW:");
        StdDraw.text(x, 23, "A short game by Jonathan Atkins and Jake Webster");
        StdDraw.setPenColor(Color.green);
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

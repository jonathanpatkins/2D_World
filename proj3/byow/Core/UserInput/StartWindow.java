package byow.Core.UserInput;

import byow.Core.Engine;
import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class StartWindow {

    /**
     * Generates a start window for game
     */
    private TERenderer ter;

    public StartWindow(TERenderer ter) {
        this.ter = ter;
    }


    public String start() {

        ter = new TERenderer();
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);



        // setting up canvas
        // StdDraw.setCanvasSize(WIDTH, HEIGHT);
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.enableDoubleBuffering();

        // draw initial frame
        drawCanvas("");


        // allow for keyboard input
        InputSource inputSource = new KeyboardInputSource();

        // these will hold our seed
        String seed = "";
        String displaySeed = "";

        // this flag will turn true when 'N' is inputted. This means a new game has started.
        // Thus we will now begin to collect the input in the @param displaySeed so the view can see
        boolean newFlag = false;

        /**
         * Preferably in the future stuff is only displayed when you are entering the number part
         * of the seed for a new game, but for now lets just display the entire seed input
         *
         * No parsing of the seed is done in here, do it all in the engine class
         */
        while (inputSource.possibleNextInput()) {

            // get key stroke from keyboard
            char c = inputSource.getNextKey();

            // add the input to our seed value
            seed += c;
            if (c == 'N') {
                // do something
                newFlag = true;
            } else if (c == 'L') {
                // do something
                break;
            } else if (c == 'Q') {
                // do something - System.exit(0)
                break;
            } else if (c == 'S') {
                // do something
                break;
            } else if (newFlag) {
                displaySeed += c;
            }
            drawCanvas(seed);
        }

        // pass the seed to some other class where it can be processed
        return seed;

    }


    /**
     * Draw the canvas with the @param seedSoFar
     */
    private void drawCanvas(String seedSoFar) {
        // start with clear canvas
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.green);
        int x = Engine.WIDTH / 2;

        // text and initial screen
        StdDraw.text(x, 17, "(N)ew game");
        StdDraw.text(x, 16, "(L)oad");
        StdDraw.text(x, 15, "(B)uild");
        StdDraw.text(x, 14, "(Q)uit");
        StdDraw.text(x, 12, "Enter seed");

        // draw the seed we have thus far
        StdDraw.text(x, 11, seedSoFar);
        StdDraw.show();
    }
}

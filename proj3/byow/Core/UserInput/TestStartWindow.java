package byow.Core.UserInput;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class TestStartWindow {

    /**
     * Would like to convert this to a black canvas with dimensions of the world - but this will do for now
     */

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public static void main(String[] args) {

        // setting up canvas
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, 20);
        StdDraw.setYscale(0, 30);
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
            } else if (c == ':') {
                // do something - nothing?
            } else if (c == 'Q') {
                // do something
                break;
            } else if (c == 'S') {
               // do something
                break;

            } else if (newFlag){
                displaySeed += c;
            }
            drawCanvas(seed);
        }
        drawCanvas("Fuck we done");
        // pass the seed to some other class where it can be processed

        
    }

    /**
     * Draw the canvas with the @param seedSoFar
     */
    private static void drawCanvas(String seedSoFar) {
        // start with clear canvas
        StdDraw.clear();

        // text and initial screen
        StdDraw.setPenColor(Color.green);
        StdDraw.text(10, 17, "(N)ew game");
        StdDraw.text(10, 16, "(L)oad");
        StdDraw.text(10, 15, "(Q)uit");
        StdDraw.text(10, 13, "Enter seed");

        // draw the seed we have thus far
        StdDraw.text(10, 12, seedSoFar);
        StdDraw.show();

    }
}

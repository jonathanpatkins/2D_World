package byow.Core.WorldComponents;

import byow.Core.Engine;
import byow.Core.Utils.Position;
import byow.TileEngine.*;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A creative mechanic allowing players to customize the world.
 * @author Jonathan Atkins, Jake Webster 11/29/20.
 */
public class Builder implements Serializable {
    private static final String FILE_NAME = "Worlds.txt";
    private TERenderer ter;
    private TETile[][] world;
    private TETile floorType, wallType;
    private List<TETile[][]> pastStates;
    private boolean pressed;
    private int xMouse;
    private int yMouse;

    /**
     * Initializes the TERenderer to @param ter.
     * Initializes the TETile for the floor to @param floorType.
     * Initializes the TETile for the wallType to @param wallType.
     */
    public Builder(TERenderer ter, TETile floorType, TETile wallType)  {
        this.ter = ter;
        this.floorType = floorType;
        this.wallType = wallType;
        this.world = new TETile[Engine.WIDTH][Engine.HEIGHT];
        this.pastStates = new ArrayList<>();

        initiateBuild();
    }

    private void mouseUpdate() {
        this.xMouse = (int) StdDraw.mouseX();
        this.yMouse = (int) StdDraw.mouseY();
        this.pressed = StdDraw.isMousePressed();
    }

    /**
     * Used to start building the world depending on the choices of the player.
     */
    private void initiateBuild() {
        ter.initialize(Engine.WIDTH, Engine.HEIGHT + 7);

        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        boolean flag = true;
        StdDraw.enableDoubleBuffering();

        // draw the initial frame
        drawFrame();


        while (flag) {
            mouseUpdate();
            if (pressed && xMouse > 3 && xMouse < 25 && yMouse < Engine.HEIGHT + 6 && yMouse > Engine.HEIGHT + 2) {
                initiateMakeRoom();
            }
            if (pressed && xMouse > 29 && xMouse < 40 && yMouse < Engine.HEIGHT + 6 && yMouse > Engine.HEIGHT + 2) {
                initiateDoorWay();
            }
            if (pressed && xMouse > 46 && xMouse < 52 && yMouse < Engine.HEIGHT + 6 && yMouse > Engine.HEIGHT + 2) {
                initiateUndo();
                StdDraw.pause(200);
                pressed = false;
            }
            if (pressed && xMouse > 58 && xMouse < 65 && yMouse < Engine.HEIGHT + 6 && yMouse > Engine.HEIGHT + 2) {
                flag = false;
            }
        }
    }

    /**
     * Reverts world back to last state.
     */
    private void initiateUndo() {
        if (pastStates.size() > 0) {
            TETile[][] lastWorld = pastStates.remove(pastStates.size() - 1);
            this.world = lastWorld;
            drawFrame();
        } else {
            System.out.println("empty");
        }
    }


    /**
     * Draws the frame.
     */
    public void drawFrame() {
        StdDraw.clear();
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.GREEN);
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(14, Engine.HEIGHT + 4, "Add World Component");
        StdDraw.text(35, Engine.HEIGHT + 4, "Add Door");
        StdDraw.text(50, Engine.HEIGHT + 4, "Undo");
        StdDraw.text(60, Engine.HEIGHT + 4, "Done");
        StdDraw.setPenColor(Color.white);
        StdDraw.line(0, Engine.HEIGHT + 1, Engine.WIDTH, Engine.HEIGHT + 1);
        StdDraw.show();
    }

    /**
     * The player has made the intention of making a doorway.
     */
    private void initiateDoorWay() {
        boolean flag = true;
        Position startingPos;

        while (flag) {
            mouseUpdate();
            startingPos = new Position(xMouse, yMouse);
            if (pressed && Engine.inBounds(startingPos) && world[xMouse][yMouse].equals(wallType)) {
                saveState();
                world[xMouse][yMouse] = floorType;
                flag = false;
            }
        }
        drawFrame();
    }

    /**
     * The player has made the intention of making a room.
     */
    public void initiateMakeRoom() {
        boolean flag = true;
        Position startingPos = null;
        Position endingPos = null;

        while (flag) {
            mouseUpdate();
            startingPos = new Position(xMouse, yMouse);
            boolean pressed = StdDraw.isMousePressed();
            if (pressed && Engine.inBounds(startingPos)) {
                while (pressed) {
                    mouseUpdate();
                    endingPos = new Position(xMouse, yMouse);
                    drawState(startingPos, endingPos);
                    StdDraw.pause(50);
                }
                flag = false;
            }
            mouseUpdate();
            endingPos = new Position(xMouse, yMouse);
        }

        makeRoom(startingPos, endingPos);
        drawFrame();
    }

    /**
     * As the player makes a room, this shows the dimensions.
     */
    private void drawState(Position startingPos, Position endingPos) {
        drawFrame();

        double x0 = Math.min(startingPos.getX(), endingPos.getX());
        double y0 = Math.min(startingPos.getY(), endingPos.getY());
        // int x1 = Math.max(startingPos.getX(), endingPos.getX());
        // int y1 = Math.max(startingPos.getY(), endingPos.getY());

        StdDraw.setPenColor(Color.GREEN);
        double width = Math.abs(startingPos.getX() - endingPos.getX()) + 1;
        double height = Math.abs(startingPos.getY() - endingPos.getY()) + 1;
        StdDraw.filledRectangle(width / 2 + x0, height / 2 + y0, width / 2, height / 2);

        StdDraw.show();
    }

    /**
     * Places a new room on screen with corners at
     * @param startingPos and @param endingPos
     */
    public void makeRoom(Position startingPos, Position endingPos) {
        Room tiles = new Room(startingPos, endingPos);

        saveState();

        if (tiles.getWallLocation() != null) {
            for (Position p : tiles.getWallLocation()) {
                if (Engine.inBounds(p)) {
                    world[p.getX()][p.getY()] = this.wallType;
                }
            }
        }
        if (tiles.getFloorLocation() != null) {
            for (Position p : tiles.getFloorLocation()) {
                if (Engine.inBounds(p)) {
                    if (p.getX() == 0 || p.getY() == 0 || p.getX() == Engine.WIDTH - 1
                            || p.getY() == Engine.HEIGHT - 1) {
                        world[p.getX()][p.getY()] = this.wallType;
                    } else {
                        world[p.getX()][p.getY()] = this.floorType;
                    }
                }
            }
        }
    }

    /**
     * Saves the current state of world.
     */
    public void saveState() {
        File myObj = new File(FILE_NAME);

        try {
            FileOutputStream file = new FileOutputStream(FILE_NAME);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // write the objects to the file
            out.writeObject(this.world);

            // close file when done
            out.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }

        try {
            FileInputStream file = new FileInputStream(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(file);

            TETile[][] temp = (TETile[][]) in.readObject();
            this.pastStates.add(temp);

            // once done using a file, always close
            file.close();
            in.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    /**
     * Gets the world state.
     * @return
     */
    public TETile[][] getWorld() {
        return world;
    }
}



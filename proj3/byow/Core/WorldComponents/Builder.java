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
        drawFrame();


        while (flag) {
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            boolean pressed = StdDraw.isMousePressed();
            if (pressed && x > 3 && x < 25 && y < Engine.HEIGHT + 6 && y > Engine.HEIGHT + 2) {
                System.out.println("1st");
                initiateMakeRoom();
            }
            if (pressed && x > 29 && x < 40 && y < Engine.HEIGHT + 6 && y > Engine.HEIGHT + 2) {
                System.out.println("3rd");
                initiateDoorWay();
            }
            if (pressed && x > 46 && x < 52 && y < Engine.HEIGHT + 6 && y > Engine.HEIGHT + 2) {
                System.out.println("4th");
                initiateUndo();
                StdDraw.pause(200);
                pressed = false;
            }
            if (pressed && x > 58 && x < 65 && y < Engine.HEIGHT + 6 && y > Engine.HEIGHT + 2) {
                System.out.println("5th");
                flag = false;
            }
        }
    }

    private void initiateUndo() {
        if (pastStates.size() > 0) {
            TETile[][] lastWorld = pastStates.remove(pastStates.size() - 1);
            this.world = lastWorld;
            drawFrame();
        } else {
            System.out.println("empty");
        }
    }


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

    private void initiateDoorWay() {
        boolean flag = true;
        Position startingPos;

        while (flag) {
            boolean pressed = StdDraw.isMousePressed();
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            startingPos = new Position(x, y);
            if (pressed && Engine.inBounds(startingPos) && world[x][y].equals(wallType)) {
//                TETile[][] temp = world.clone();
//                pastStates.add(temp);
                saveState();
                world[x][y] = floorType;
                flag = false;
            }
        }
        drawFrame();
    }

    public void initiateMakeRoom() {
        boolean flag = true;
        Position startingPos = null;
        Position endingPos = null;

        while (flag) {
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            startingPos = new Position((int) x, (int) y);
            boolean pressed = StdDraw.isMousePressed();
            if (pressed && Engine.inBounds(startingPos)) {
                while (pressed) {
                    pressed = StdDraw.isMousePressed();
                    x = StdDraw.mouseX();
                    y = StdDraw.mouseY();
                    endingPos = new Position((int) x, (int) y);
                    drawState(startingPos, endingPos);
                    StdDraw.pause(50);
                }
                flag = false;
            }
            x = StdDraw.mouseX();
            y = StdDraw.mouseY();
            endingPos = new Position((int) x, (int) y);
        }

        makeRoom(startingPos, endingPos);
        drawFrame();
    }

    private void drawState(Position startingPos, Position endingPos) {
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

        double x0 = Math.min(startingPos.getX(), endingPos.getX());
        double y0 = Math.min(startingPos.getY(), endingPos.getY());
        int x1 = Math.max(startingPos.getX(), endingPos.getX());
        int y1 = Math.max(startingPos.getY(), endingPos.getY());


//        double[] lowerLeft = {x0, y0};
//        double[] lowerRight = {x1, y0};
//        double[] upperLeft = {x0, y1};
//        double[] upperRight = {x1, y1};
//        double[] x = {x0, x0+5, x1, x1+.01};
//        double[] y = {y0, y0+7, y1, y1+.01};


        StdDraw.setPenColor(Color.GREEN);
//        StdDraw.filledPolygon(x, y);
        double width = Math.abs(startingPos.getX() - endingPos.getX()) + 1;
        double height = Math.abs(startingPos.getY() - endingPos.getY()) + 1;
        StdDraw.filledRectangle(width / 2 + x0, height / 2 + y0, width / 2, height / 2);







        StdDraw.show();
    }

    public void makeRoom(Position startingPos, Position endingPos) {
        Room tiles = new Room(startingPos, endingPos);

//        TETile[][] temp = world.clone();
//        pastStates.add(temp);
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
                    world[p.getX()][p.getY()] = this.floorType;
                }
            }
        }
        System.out.println("h");
    }

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

    public TETile[][] getWorld() {
        return world;
    }
}



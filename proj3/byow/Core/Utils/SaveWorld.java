package byow.Core.Utils;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.io.*;
import java.util.Random;
import java.util.ArrayList;

/**
 * A class that allows one to save the current state of the world for future use.
 * @author Jonathan Atkins, Jake Webster 11/21/20.
 */
public class SaveWorld implements Serializable {
    private TETile[][] world;
    private Position posOfAvatar, power, heart;
    private Random random;
    private TERenderer ter;
    private TETile floorType, wallType;
    private ArrayList<Position> enemies;
    private int lives;
    private boolean powered, boosted, togglePaths;

    /**
     * Generate the instance variables using their corresponding values in @param loadedObjects.
     */
    public SaveWorld(ArrayList<Object> loadedObjects) {
        this.ter = (TERenderer) loadedObjects.get(0);
        this.world = (TETile[][]) loadedObjects.get(1);
        this.posOfAvatar = (Position) loadedObjects.get(2);
        this.random = (Random) loadedObjects.get(3);
        this.floorType = (TETile) loadedObjects.get(4);
        this.wallType = (TETile) loadedObjects.get(5);
        this.enemies = (ArrayList<Position>) loadedObjects.get(6);
        this.power = (Position) loadedObjects.get(7);
        this.heart = (Position) loadedObjects.get(8);
        this.lives = (int) loadedObjects.get(9);
        this.powered = (boolean) loadedObjects.get(10);
        this.boosted = (boolean) loadedObjects.get(11);
        this.togglePaths = (boolean) loadedObjects.get(12);

        writeInfo();
    }

    /**
     * Creates a .txt file "World.txt" and writes essential info so that the game can
     * later be resumed from the current state.
     */
    public void writeInfo() {
        String filename = "World.txt";
        File myObj = new File(filename);

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Write the objects to the file.
            out.writeObject(this.ter);
            out.writeObject(this.world);
            out.writeObject(this.posOfAvatar);
            out.writeObject(this.random);
            out.writeObject(this.floorType);
            out.writeObject(this.wallType);
            out.writeObject(this.enemies);
            out.writeObject(this.power);
            out.writeObject(this.heart);
            out.writeObject(this.lives);
            out.writeObject(this.powered);
            out.writeObject(this.boosted);
            out.writeObject(this.togglePaths);

            // Close file when done.
            out.close();
            file.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }
    }
}

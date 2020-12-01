package byow.Core.Utils;

import byow.TileEngine.*;
import byow.Core.WorldComponents.World;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;

/**
 * Loads the world form the World.txt file.
 * @author Jonathan Atkins, Jake Webster 11/21/20.
 */
public class LoadWorld implements Serializable {

    private TETile[][] world;
    private Position avatar, power, heart;
    private Random random;
    private TERenderer ter;
    private TETile floorType, wallType;
    private ArrayList<Position> enemies;
    private int lives;
    private boolean powered, boosted, togglePaths;
    private ArrayList<Object> objects;

    /**
     * Examines the .txt file with saved info from the most recent saved gamestate, and
     * sets key game objects equal to their previous values.
     */
    public LoadWorld() {
        String filename = "World.txt";

        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Gets the objects that were written to World.txt.
            this.ter = (TERenderer) in.readObject();
            this.world = (TETile[][]) in.readObject();
            this.avatar = (Position) in.readObject();
            this.random = (Random) in.readObject();
            this.floorType = (TETile) in.readObject();
            this.wallType = (TETile) in.readObject();
            this.enemies = (ArrayList) in.readObject();
            this.power = (Position) in.readObject();
            this.heart = (Position) in.readObject();
            this.lives = (int) in.readObject();
            this.powered = (boolean) in.readObject();
            this.boosted = (boolean) in.readObject();
            this.togglePaths = (boolean) in.readObject();

            this.objects =  new ArrayList<>();
            objects.add(ter);
            objects.add(world);
            objects.add(avatar);
            objects.add(random);
            objects.add(floorType);
            objects.add(wallType);
            objects.add(enemies);
            objects.add(power);
            objects.add(heart);
            objects.add(lives);
            objects.add(powered);
            objects.add(boosted);
            objects.add(togglePaths);

            // Close once done with the file.
            file.close();
            in.close();

        } catch (IOException ex) {
            System.out.println("IOException is caught");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    /**
     * Reload the world by creating a World instance with the generated objects.
     */
    public void load() {
        World loadedWorld = new World(objects);
    }

    /**
     * @Return the world state based on the world array.
     */
    public TETile[][] getWorld() {
        return world;
    }

    /**
     * @Return the position of the avatar.
     */
    public Position getAvatar() {
        return avatar;
    }

    /**
     * @Return the Random object.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @Return the TERenderer object.
     */
    public TERenderer getTer() {
        return ter;
    }

    /**
     * @Return the list of objects pulled from the .txt file.
     */
    public ArrayList<Object> getObjects() {
        return objects;
    }
}

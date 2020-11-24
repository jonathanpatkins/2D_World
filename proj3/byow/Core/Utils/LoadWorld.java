package byow.Core.Utils;

import byow.Core.Engine;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.WorldComponents.World;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public class LoadWorld implements Serializable{

    /**
     * Loads the world form the World.txt file
     */

    TETile[][] world;
    Position avatar;
    Random random;
    TERenderer ter;
    TETile floorType, wallType;

    public LoadWorld() {
        String filename = "C:\\Users\\Jonathan\\cs61b\\fa20-proj3-g523\\proj3\\byow\\Core\\World.txt";

        try {
            // No idea what this is for, just patterned matched stuff
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Gets the objects that were written to World.txt
            this.ter = (TERenderer) in.readObject();
            this.world = (TETile[][]) in.readObject();
            this.avatar = (Position) in.readObject();
            this.random = (Random) in.readObject();
            this.floorType = (TETile) in.readObject();
            this.wallType = (TETile) in.readObject();
            System.out.println(floorType);

            // once done using a file, always close
            file.close();
            in.close();



        } catch (IOException ex) {
            System.out.println("IOException is caught");
        }
        catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException is caught");
        }
    }

    public void load() {
        // reload the world
        ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        World loadedWorld = new World(ter, world, random, avatar, floorType, wallType);
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Position getAvatar() {
        return avatar;
    }

    public Random getRandom() {
        return random;
    }

    public TERenderer getTer() {
        return ter;
    }
}

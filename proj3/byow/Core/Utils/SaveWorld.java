package byow.Core.Utils;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.*;
import java.util.Random;
import java.util.ArrayList;

public class SaveWorld implements Serializable{

    TETile[][] world;
    Position posOfAvatar, power;
    Random random;
    TERenderer ter;
    TETile floorType, wallType;
    ArrayList<Position> enemies;
    int lives;
    boolean powered;

    //TODO: We need to change the constructor/call of this class. Too many inputs for the style guide.
    public SaveWorld(TERenderer ter, TETile[][] world, Position posOfAvatar, Random random,
                     TETile f, TETile w, ArrayList<Position> enemies, Position power, int lives, boolean powered) {

        /**
         * Saves the info of the world to World.txt
         */


        // all the info I want to save
        this.world = world;
        this.posOfAvatar = posOfAvatar;
        this.random = random;
        this.ter = ter;
        this.floorType = f;
        this.wallType = w;
        this.enemies = enemies;
        this.power = power;
        this.lives = lives;
        this.powered = powered;

        // create file
        String filename = "World.txt";
        File myObj = new File(filename);

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // write the objects to the file
            out.writeObject(this.ter);
            out.writeObject(this.world);
            out.writeObject(this.posOfAvatar);
            out.writeObject(this.random);
            out.writeObject(this.floorType);
            out.writeObject(this.wallType);
            out.writeObject(this.enemies);
            out.writeObject(this.power);
            out.writeObject(this.lives);
            out.writeObject(this.powered);

            // close file when done
            out.close();
            file.close();


        }
        catch(IOException ex) {
            System.out.println("IOException is caught");
        }

    }

}

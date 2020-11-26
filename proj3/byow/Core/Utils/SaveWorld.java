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

    public SaveWorld(ArrayList<Object> loadedObjects) {
        this.ter = (TERenderer) loadedObjects.get(0);
        this.world = (TETile[][]) loadedObjects.get(1);
        this.posOfAvatar = (Position) loadedObjects.get(2);
        this.random = (Random) loadedObjects.get(3);
        this.floorType = (TETile) loadedObjects.get(4);
        this.wallType = (TETile) loadedObjects.get(5);
        this.enemies = (ArrayList<Position>) loadedObjects.get(6);
        this.power = (Position) loadedObjects.get(7);
        this.lives = (int) loadedObjects.get(8);
        this.powered = (boolean) loadedObjects.get(9);

        writeInfo();
    }

    public void writeInfo() {
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

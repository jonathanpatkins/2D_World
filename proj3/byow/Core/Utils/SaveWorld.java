package byow.Core.Utils;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.*;
import java.util.Random;

public class SaveWorld implements Serializable{

    TETile[][] world;
    Position posOfAvatar;
    Random random;
    TERenderer ter;

    public SaveWorld(TERenderer ter, TETile[][] world, Position posOfAvatar, Random random) {

        /**
         * Saves the info of the world to World.txt
         */


        // all the info I want to save
        this.world = world;
        this.posOfAvatar = posOfAvatar;
        this.random = random;
        this.ter = ter;





        // Use your own path file to the Core directory - this one is specific to my own computer
        String filename = "C:\\Users\\Jonathan\\cs61b\\fa20-proj3-g523 - Copy (3)\\proj3\\byow\\Core\\World.txt";
        File myObj = new File(filename);

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // write the objects to the file
            out.writeObject(ter);
            out.writeObject(world);
            out.writeObject(posOfAvatar);
            out.writeObject(random);


            // close file when done
            out.close();
            file.close();


        }
        catch(IOException ex) {
            System.out.println("IOException is caught");
        }

    }

}

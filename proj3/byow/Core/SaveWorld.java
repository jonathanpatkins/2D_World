package byow.Core;

import byow.Core.TileEngine.TERenderer;
import byow.Core.TileEngine.TETile;

import java.io.*;
import java.util.Random;

public class SaveWorld implements Serializable{

    TETile[][] world;
    Position posOfAvatar;
    Random random;
    TERenderer ter;
    TETile x;

    public SaveWorld(TERenderer ter, TETile[][] world, Position posOfAvatar, Random random, TETile x) {

        /**
         * Saves the info of the world to World.txt
         */


        // all the info I want to save
        this.world = world;
        this.posOfAvatar = posOfAvatar;
        this.random = random;
        this.ter = ter;
        this.x = x;





        // Use your own path file to the Core directory - this one is specific to my own computer
        String filename = "C:\\Users\\Jonathan\\cs61b\\fa20-proj3-g523 - Copy\\proj3\\byow\\Core\\World.txt";
        File myObj = new File(filename);

        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // write the objects to the file
            out.writeObject(ter);
            out.writeObject(world);
            out.writeObject(posOfAvatar);
            out.writeObject(random);
            out.writeObject(x);


            // close file when done
            out.close();
            file.close();

            // once done with all of that quit the program
            System.exit(0);
        }
        catch(IOException ex) {
            System.out.println("IOException is caught");
        }

    }

}

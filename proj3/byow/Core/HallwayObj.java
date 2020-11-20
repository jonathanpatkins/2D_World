//KEEP
package byow.Core;

import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;

public class HallwayObj {

    /**
     * Just want to keep track of the floor, wall, length, width and the corner (this is for collisions later)
     */
    List<Position> floor;
    List<Position> wall;
    Position corner;
    int length;
    int width;

    /**
     * Constructor for vertical and horiz halls
     * @param floor
     * @param wall
     * @param length
     * @param width
     */
    public HallwayObj(List<Position> floor, List<Position> wall, int length, int width) {
        this.floor = floor;
        this.wall = wall;
        this.length = length;
        this.width = width;
    }
    @Override
    public String toString() {
        return "len: " + length + " and width: " + width;
    }


    public List<Position> getWall() {
        return wall;
    }

    public List<Position> getFloor() {
        return floor;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

}

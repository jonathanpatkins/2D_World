package byow.Core;

import java.util.List;

/**
 * A class that can be used to help generate hallways.
 * @author Jonathan Atkins, Jake Webster 11/17/20.
 */
public class HallwayObj {

    /**
     * @param floor: The floor Positions of a Hallway.
     * @param wall: The wall Positions of a Hallway.
     * @param corner: The corner of a Hallway.
     * @param length: The length (y) of a Hallway.
     * @param width: The width (x) of a Hallway.
     */
    private List<Position> floor, wall;
    private Position corner;
    private int length, width;

    /**
     * Constructs vertical and horizontal walls.
     * @param f sets the floor of this class.
     * @param w sets the wall of this class.
     * @param len sets the length of this class.
     * @param wth sets the width of this class.
     */
    public HallwayObj(List<Position> f, List<Position> w, int len, int wth) {
        this.floor = f;
        this.wall = w;
        this.length = len;
        this.width = wth;
    }

    /**
     * @Return @param wall.
     */
    public List<Position> getWall() {
        return wall;
    }

    /**
     * @Return @param floor.
     */
    public List<Position> getFloor() {
        return floor;
    }

    /**
     * @Return @param length.
     */
    public int getLength() {
        return length;
    }

    /**
     * @Return @param width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @Return a simple String representation of the class.
     */
    @Override
    public String toString() {
        return "len: " + length + " and width: " + width;
    }
}

package byow.Core;

import java.util.ArrayList;
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
     * @param floor sets the floor of this class.
     * @param wall sets the wall of this class.
     * @param length sets the length of this class.
     */
    public HallwayObj(List<Position> floor, List<Position> wall, int length, int width) {
        this.floor = floor;
        this.wall = wall;
        this.length = length;
        this.width = width;
    }

    /**
     * Constructor for when you want to make a curved hall
     * @param a is the first HallwayObj being considered.
     * @param b is the second HallwayObj being considered.
     * @param length sets the length of this class.
     * @param width sets the width of this class.
     * @param corner sets the corner of this class.
     */
    public HallwayObj(HallwayObj a, HallwayObj b, int length, int width, Position corner) {
        List<Position> aFloor = a.getFloor();
        List<Position> aWall = a.getWall();
        List<Position> bFloor = b.getFloor();
        List<Position> bWall = b.getWall();

        // These are floor locations that MIGHT get covered up by walls during the merge.
        Position afirstFloor = aFloor.get(0);
        Position bfirstFloor = bFloor.get(0);

        // Used to add things without duplicates.
        this.floor = new ArrayList<>();
        this.floor.addAll(aFloor);

        for (Position j: bFloor) {
            if (!this.floor.contains(j)) {
                this.floor.add(j);
            }
        }

        this.wall = new ArrayList<>();
        this.wall.addAll(aWall);

        for (Position j: bWall) {
            if (!this.wall.contains(j)) {
                this.wall.add(j);
            }
        }

        // This removes the wall that will later be replaced by floor when the 2nd hallway
        // merges with the first.
        this.wall.remove(afirstFloor);
        this.wall.remove(bfirstFloor);

        this.length = length;
        this.width = width;
        this.corner = corner;
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
     * @Return @param corner.
     */
    public Position getCorner() {
        return corner;
    }
}

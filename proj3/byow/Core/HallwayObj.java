package byow.Core;

import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;

public class HallwayObj {
    List<Position> floor;
    List<Position> wall;
    Position corner;
    int length;
    int width;

    /**
     * Todo: need to keep track of corners for this new hallway;
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

    public HallwayObj(HallwayObj a, HallwayObj b, int length, int width, Position corner) {
        List<Position> aFloor = a.getFloor();
        List<Position> aWall = a.getWall();
        List<Position> bFloor = b.getFloor();
        List<Position> bWall = b.getWall();

        Position afirstFloor = aFloor.get(0);
        Position bfirstFloor = bFloor.get(0);

        this.floor = new ArrayList<>();
        for (Position i : aFloor) {
            this.floor.add(i);
        }
        for (Position j : bFloor) {
            if (!this.floor.contains(j)) {
                this.floor.add(j);
            }
        }

        this.wall = new ArrayList<>();
        for (Position i : aWall) {
            this.wall.add(i);
        }
        for (Position j : bWall) {
            if (!this.wall.contains(j)) {
                this.wall.add(j);
            }
        }

        // this removes the wall that will later be replaced by floor when the 2nd hallway merges with the first
        if (this.wall.contains(afirstFloor)) {
            this.wall.remove(afirstFloor);
        }
        if (this.wall.contains(bfirstFloor)) {
            this.wall.remove(bfirstFloor);
        }

        this.length = length;
        this.width = width;
        this.corner = corner;
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

    public Position getCorner() {
        return corner;
    }
}

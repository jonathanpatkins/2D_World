package byow.Core;

import byow.lab12.Position;

import java.util.ArrayList;
import java.util.List;

public class HallwayObj {
    List<Position> floor;
    List<Position> wall;
    int length;
    int width;

    public HallwayObj(List<Position> floor, List<Position> wall, int length, int width) {
        this.floor = floor;
        this.wall = wall;
        this.length = length;
        this.width = width;
    }

    public HallwayObj(HallwayObj a, HallwayObj b, int length, int width) {
        List<Position> aFloor = a.getFloor();
        List<Position> aWall = a.getWall();
        List<Position> bFloor = b.getFloor();
        List<Position> bWall = b.getWall();

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

        this.length = length;
        this.width = width;
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

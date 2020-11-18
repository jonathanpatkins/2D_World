package byow.Core;

import byow.TileEngine.TETile;
import byow.lab12.Position;

import java.util.List;

public class Hallway {
    /**
     * Should take in parameters the TetTile[][] world and the rooms and the UnionFind Obj
     *
     * should have Methods:
     *  make vertical hall
     *  make horiz hadd
     *      these methods should only take in a starting point and they should keep on building up until they hit
     *      another wall. If that other wall is a corner - return false or something and do not make the wall,
     *      otherwise make the wall and add it (maybe have a different class for a hall object) to the component list
     *      in UnionFind
     */
    TETile[][] world;
    List<RoomAdj> rooms;

    public Hallway(TETile[][] world, List<RoomAdj> rooms, UnionFind obj) {
        this.world = world;
        this.rooms = rooms;
    }

    // this will operate on the basis that the starting position is a floor tile
    public boolean makeVerticalHall(Position i) {
        return false;
    }
    public boolean makeHorizontalHall(Position i) {
        return false;
    }
    /**
     * Then there should be some methods for L shaped halls
     *  This reduces to making a vertical hall and then a horizontal hall or vice versa with minor adjustments
     */
}

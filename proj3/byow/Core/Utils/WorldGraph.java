package byow.Core.Utils;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.ArrayList;

public class WorldGraph implements AStarGraph {
    private TETile[][] world;
    private TETile wallType;

    public WorldGraph(TETile[][] grid, TETile w) {
        world = grid;
        wallType = w;
    }

    public List<WeightedEdge> neighbors(Position v) {
        List<WeightedEdge> myList = new ArrayList<>();
        Position up = new Position(v, 0, 1);
        if (checkValid(up)) {
            myList.add(new WeightedEdge(v, up, 1));
        }
        Position down = new Position(v, 0, -1);
        if (checkValid(down)) {
            myList.add(new WeightedEdge(v, down, 1));
        }
        Position left = new Position(v, -1, 0);
        if (checkValid(left)) {
            myList.add(new WeightedEdge(v, left, 1));
        }
        Position right = new Position(v, 1, 0);
        if (checkValid(right)) {
            myList.add(new WeightedEdge(v, right, 1));
        }
        return myList;
    }
    public double estimatedDistanceToGoal(Position s, Position goal) {
        double xDist = Math.abs(s.getX() - goal.getX());
        double yDist = Math.abs(s.getY() - goal.getY());
        return xDist + yDist;
    }

    private boolean checkValid(Position p) {
        TETile tile = world[p.getX()][p.getY()];
        return tile != wallType && tile != Tileset.NOTHING; // && tile != Tileset.ENEMY;
    }
}

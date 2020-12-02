package byow.Core.Utils;

import byow.Core.Engine;
import byow.TileEngine.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Creates a WorldGraph, which shows the appropriate points for the AStarSolver to consider.
 * @author Jonathan Atkins, Jake Webster 11/28/20.
 * @source this was inspired by graphs in proj2c and proj2d.
 */
public class WorldGraph implements AStarGraph {
    private TETile[][] world;
    private TETile wallType;

    /**
     * Initialize the world array to be considered using @param grid.
     * Initialize the wall type to look out for using @param w.
     */
    public WorldGraph(TETile[][] grid, TETile w) {
        world = grid;
        wallType = w;
    }

    /**
     * @Return a list of neighbors from @param v.
     * Neighbors are weighted edges consisting of a point and a distance from @param v.
     * In this case, all WeightedEdge objects have a distance of 1, as they are neighbors.
     * Only tiles that can be moved onto are considered as neighbors.
     */
    @Override
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

    /**
     * @Return the distance heuristic from @param s and @param goal.
     * In this case, the distance estimate is the absolute difference of
     * x and y coordinates of the start and goal positions.
     */
    @Override
    public double estimatedDistanceToGoal(Position s, Position goal) {
        double xDist = Math.abs(s.getX() - goal.getX());
        double yDist = Math.abs(s.getY() - goal.getY());
        return xDist + yDist;
    }

    /**
     * @Return whether @param p can be moved onto.
     * Walls and NOTHING cannot be moved onto, the rest is generally fair game.
     */
    private boolean checkValid(Position p) {
        if (!Engine.inBounds(p)) {
            return false;
        }
        TETile tile = world[p.getX()][p.getY()];
        return !tile.equals(wallType) && !tile.equals(Tileset.NOTHING);
    }
}

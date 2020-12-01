package byow.Core.Utils;
import java.util.List;

/**
 * An interface that can be used in implementing the A* algorithm.
 * @author Jake Webster, 61b course staff/Hug 11/03/20.
 * @source proj2c.
 */
public interface AStarGraph {
    List<WeightedEdge> neighbors(Position v);
    double estimatedDistanceToGoal(Position s, Position goal);
}

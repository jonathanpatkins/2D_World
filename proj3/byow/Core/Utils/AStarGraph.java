package byow.Core.Utils;
import java.util.List;

/**
 * @source proj2c.
 */
public interface AStarGraph {
    List<WeightedEdge> neighbors(Position v);
    double estimatedDistanceToGoal(Position s, Position goal);
}

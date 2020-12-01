package byow.Core.Utils;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * An application of the A* algorithm that allows one to do some neat stuff.
 * @author Jake Webster 11/3/20.
 *
 * NOTE: According to Ed, it is okay to use data structures from previous projects here
 * so long as we are both not resubmitting it, which I have confirmed with Jonathan.
 * @source https://us.edstem.org/courses/979/discussion/174505?comment=433526.
 */
public class AStarSolver {
    private ArrayList<Position> solution;
    private HashMap<Position, Double> distTo;
    private HashMap<Position, Position> edgeTo;
    private double weight, time;
    private int states;
    private DoubleMapPQ<Position> queue;
    private Stopwatch watch;
    private SolverOutcome outcome;
    /**
     * Finds the A* solution for Graph @param input from @param start
     * to @param end. If it takes longer than @param timeout, this is
     * reflected by using SolverOutcome.TIMEOUT. If there is no solution,
     * it returns @SolverOutcome.UNSOLVABLE.
     */
    public AStarSolver(AStarGraph input, Position start, Position end, double timeout) {
        watch = new Stopwatch();
        queue = new DoubleMapPQ<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        solution = new ArrayList<>();
        queue.add(start, input.estimatedDistanceToGoal(start, end));
        distTo.put(start, 0.0);
        while (queue.size() != 0 && watch.elapsedTime() < timeout) {
            Position current = queue.removeSmallest();
            if (current.equals(end)) {
                outcome = SolverOutcome.SOLVED;
                time = watch.elapsedTime();
                weight = distTo.get(end);
                generateSolution(start, current);
                return;
            }
            states += 1;
            for (WeightedEdge edge: input.neighbors(current)) {
                relax(edge, input, end);
            }
        }
        if (queue.size() == 0) {
            setNotFound(SolverOutcome.UNSOLVABLE);
        }
        if (watch.elapsedTime() > timeout) {
            setNotFound(SolverOutcome.TIMEOUT);
        }
    }

    /**
     * Sets variables to their not found state.
     * The outcome is set to @param s.
     */
    private void setNotFound(SolverOutcome s) {
        outcome = s;
        solution = new ArrayList<>();
        weight = 0.0;
        time = watch.elapsedTime();
    }

    /**
     * Relaxes an @param edge if necessary.
     * Estimates distance to @param end using @param input.
     */
    private void relax(WeightedEdge edge, AStarGraph input, Position end) {
        Position from = edge.from();
        Position to = edge.to();
        double wht = edge.weight();
        if (!distTo.containsKey(to) || distTo.get(from) + wht < distTo.get(to)) {
            distTo.put(to, distTo.get(from) + wht);
            edgeTo.put(to, from);
            double newDist = input.estimatedDistanceToGoal(to, end);
            if (queue.contains(to)) {
                queue.changePriority(to, distTo.get(to) + newDist);
            } else {
                queue.add(to, distTo.get(to) + newDist);
            }
        }
    }

    /**
     * Recursively generates the solution sequence starting at @param v,
     * the end of the vertex, and ending at @param start, the starting vertex.
     */
    private void generateSolution(Position start, Position v) {
        if (v.equals(start)) {
            solution.add(0, start);
            return;
        } else {
            solution.add(0, v);
            Position next = edgeTo.get(v);
            generateSolution(start, next);
        }
    }

    /**
     * @return SolverOutcome.SOLVED if successfully completed in time,
     * SolverOutcome.TIMEOUT if it ran out of time, or SolverOutcome.UNSOLVABLE
     * if the priority queue was empty before solving.
     */
    public SolverOutcome outcome() {
        System.out.println(outcome);
        return outcome;
    }

    /**
     * @return a list reflecting the solution to A*. If TIMEOUT or UNSOLVABLE,
     * the list and thus return value will be empty.
     */
    public List<Position> solution() {
        return solution;
    }

    /**
     * @return the total weight of the solution, or zero if TIMEOUT or UNSOLVABLE.
     */
    public double solutionWeight() {
        return weight;
    }

    /**
     * @return the number of removals from the PriorityQueue.
     */
    public int numStatesExplored() {
        return states;
    }

    /**
     * @return the time (in seconds) taken.
     */
    public double explorationTime() {
        return time;
    }
}

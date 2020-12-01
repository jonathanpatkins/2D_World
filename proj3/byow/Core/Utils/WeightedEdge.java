package byow.Core.Utils;

/**
 * Utility class that represents a weighted edge.
 * @author Hug.
 * @source project 2c.
 */
public class WeightedEdge {
    private Position v;
    private Position w;
    private double weight;

    private String name;

    public WeightedEdge(Position v, Position w, double weight) {
        this.v = v;
        this.w = w;
        this.weight = weight;
    }
    public Position from() {
        return v;
    }
    public Position to() {
        return w;
    }
    public double weight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

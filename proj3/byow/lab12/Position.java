package byow.lab12;

public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position p, int xOffset, int yOffset) {
        x = p.getX() + xOffset;
        y = p.getY() + yOffset;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

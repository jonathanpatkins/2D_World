package byow.Core;

public class TestUnionFind {
    public static void main(String[] args) {
        UnionFind x = new UnionFind();
        x.addComponent(new Object());
        x.addComponent(new Object());
        x.addComponent(new Object());

        x.connect(1, 2);
    }
}

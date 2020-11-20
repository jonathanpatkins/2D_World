package byow.Core;

import java.util.ArrayList;
import java.util.List;

/**
 * A variation on the UnionFind data structure used to ensure Room objects are connected.
 * @author Jonathan Atkins, Jake Webster 11/17/20
 * @source Lab 6.
 */
public class UnionFind {
    /**
     * Takes in nothing. You can then add component objects through addComponent.
     * With those components you can connect them and see if they are all connected.
     * @param parent and @param component are the same size- they represent same objects.
     * @param dummyNode is used to see connections, similar to the concept from percolation.
     * @source on dummyNode concept: hw2.
     */
    private List<Integer> parent;
    private List<Object> component;
    private Object dummyNode;

    /**
     * Creates a UnionFind data structure holding n vertices. Initially, all
     * vertices are in disjoint sets.
     */
    public UnionFind() {
        parent = new ArrayList<>();
        component = new ArrayList<>();
        dummyNode = new Object();
        parent.add(-1);
        component.add(dummyNode);
    }

    /**
     * Adds @param a to the UnionFind.
     */
    public void addComponent(Object a) {
        parent.add(-1);
        component.add(a);
        if (parent.get(0) == -1) {
            connect(dummyNode, a);
        }
    }

    /**
     * Throws an exception if @param v1 is not a valid vertex.
     */
    private void validate(int v1) {
        if (v1 >= parent.size()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @Return the size of the set @param v1 belongs to.
     */
    private int sizeOf(int v1) {
        int root = find(v1);
        return -1 * parent(root);
    }

    /**
     * @Return the parent of @param v1. If @param v1 is the root of a tree,
     * @Return the negative size of the tree for which @param v1 is the root.
     */
    private int parent(int v1) {
        validate(v1);
        return parent.get(v1);
    }

    /**
     * @Return true if ints @param v1 and @param v2 are connected.
     */
    public boolean isConnected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /**
     * @Return whether objects @param v1 and @param v2 are connected.
     */
    public boolean isConnected(Object v1, Object v2) {
        if (component.contains(v1) && component.contains(v2)) {
            int v1Index = component.indexOf(v1);
            int v2Index = component.indexOf(v2);
            return this.isConnected(v1Index, v2Index);
        }
        return false;
    }

    /**
     * Connects @param v1 and @param v2 together. Union-by-size heuristic is used.
     * Tiebreakers lead to the root of @param v1 being connected to the root of @param v2.
     * Connecting a vertex with itself won't change the sets but can change the internal
     * structure of the UnionFind.
     * @source for this criteria: lab6.
     */
    public void connect(int v1, int v2) {
        if (!isConnected(v1, v2)) {
            int v1Size = sizeOf(v1);
            int v2Size = sizeOf(v2);
            int v1Parent = find(v1);
            int v2Parent = find(v2);
            if (v1Size > v2Size) {
                parent.set(v1Parent, -1 * (v1Size + v2Size));
                parent.set(v2Parent, v1Parent);
            } else {
                parent.set(v2Parent, -1 * (v1Size + v2Size));
                parent.set(v1Parent, v2Parent);
            }
        }
    }

    /**
     * Connects Objects @param v1 and @param v2. Uses the connect(int, int) method.
     */
    public void connect(Object v1, Object v2) {
        if (!component.contains(v1) || !component.contains(v2)) {
            throw new IllegalArgumentException();
        }
        int v1Index = component.indexOf(v1);
        int v2Index = component.indexOf(v2);
        this.connect(v1Index, v2Index);
    }

    /**
     * @Return the root of the set @param v1 belongs to.
     */
    private int find(int v1) {
        validate(v1);
        int p1 = parent(v1);
        if (p1 < 0) {
            return v1;
        }
        return find(p1);
    }

    /**
     * @Return whether all elements of the UnionFind are connected.
     */
    public boolean allConnected() {
        for (Object i : component) {
            if (!isConnected(this.dummyNode, i)) {
                return false;
            }
        }
        return true;
    }
}

package byow.Core;

import java.util.ArrayList;
import java.util.List;

public class UnionFind {

    // both of these should be the same size
    List<Integer> parent;
    List<Object> component;
    Object dummyNode;

    /* Creates a UnionFind data structure holding n vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind() {
        parent = new ArrayList<>();
        component = new ArrayList<>();

        // this will be our dummy node used to see connections like in percolation hw
        dummyNode = new Object();
        addComponent(dummyNode);

    }

    public void addComponent(Object a) {
        parent.add(-1);
        component.add(a);
    }

    /* Throws an exception if v1 is not a valid vertex. */
    private void validate(int v1) {
        // TODO
        if (v1 >= parent.size()) {
            throw new IllegalArgumentException();
        }
    }

    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        int root = find(v1);
        return -1 * parent(root);
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    public int parent(int v1) {
        validate(v1);
        return parent.get(v1);
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean isConnected(int v1, int v2) {
        // TODO
        return find(v1) == find(v2) ;
    }

    /* Can now pass in Objects and will return if connected */
    public boolean isConnected(Object v1, Object v2) {
        if (component.contains(v1) && component.contains(v2)) {
            int v1Index = component.indexOf(v1);
            int v2Index = component.indexOf(v2);
            return this.isConnected(v1Index, v2Index);
        }
        return false;
    }

    /* Connects two elements v1 and v2 together. v1 and v2 can be any valid
       elements, and a union-by-size heuristic is used. If the sizes of the sets
       are equal, tie break by connecting v1's root to v2's root. Connecting a
       vertex with itself or vertices that are already connected should not
       change the sets but may alter the internal structure of the data. */
    public void connect(int v1, int v2) {
        // TODO
        // need to check if its the same vertex
        // save values so code is more reading friendly
        if (!isConnected(v1, v2)) {
            if (sizeOf(v1) > sizeOf(v2)) {
                parent.set(find(v1), -1 * (sizeOf(v1) + sizeOf(v2)));
                parent.set(find(v2), find(v1));
//                parent[find(v1)] = -1 * (sizeOf(v1) + sizeOf(v2));
//                parent[find(v2)] = find(v1);
            }
            else {
                parent.set(find(v2), -1 * (sizeOf(v1) + sizeOf(v2)));
                parent.set(find(v1), find(v2));
//                parent[find(v2)] = -1 * (sizeOf(v1) + sizeOf(v2));
//                parent[find(v1)] = find(v2);
            }
        }
    }
    /* Can now pass in objects and connect them */
    public void connect(Object v1, Object v2) {
        if (!component.contains(v1) || !component.contains(v2)) {
            throw new IllegalArgumentException();
        }
        int v1Index = component.indexOf(v1);
        int v2Index = component.indexOf(v2);
        this.connect(v1Index, v2Index);
    }

    /* Returns the root of the set v1 belongs to. Path-compression is employed
       allowing for fast search-time. */
    public int find(int v1) {
        // TODO
        validate(v1);
        if (parent(v1) < 0) {
            return v1;
        }
        return find(parent(v1));
    }

    public boolean allConnected() {
        for (Object i : component) {
            if (!isConnected(this.dummyNode, i)) {
                return false;
            }
        }
        return true;
    }

}


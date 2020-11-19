package byow.Core;

public class Pair<K, V> {
    private K k;
    private V v;
    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }
    public K getKey() {
        return k;
    }
    public V getValue() {
        return v;
    }

    /**
     * @return if @param o equals this.
     */
    @Override
    public boolean equals(Object o) {
        Pair<K, V> p = (Pair) o;
        boolean direct = k.equals(p.getKey()) && v.equals(p.getValue());

        return direct;
    }

    @Override
    public String toString() {
        return k.toString() + " , " + v.toString();
    }
}

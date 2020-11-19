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
     * Complicated for our Pair: if one key equals the other's value and vice versa,
     * the Pair is also considered equal. In other words, it doesn't matter which
     * is the key and which is the value.
     */
    @Override
    public boolean equals(Object o) {
        Pair<K, V> p = (Pair) o;
        boolean direct = k.equals(p.getKey()) && v.equals(p.getValue());
        boolean indirect = k.equals(p.getValue()) && v.equals(p.getKey());
        boolean check = direct ;//|| indirect;
        return direct;
    }

    public boolean equalKey(K o) {
        return k.equals(o);
    }

    @Override
    public String toString() {
        return k.toString() + " , " + v.toString();
    }
}

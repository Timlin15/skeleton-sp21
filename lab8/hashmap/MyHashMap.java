package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private double capacity;
    private int length;
    private HashSet<K> Keys;
    // You should probably define some more!

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        size = 0;
        capacity = maxLoad;
        length = initialSize;
        Keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(16);
        size = 0;
        capacity = 0.75;
        length = 16;
        Keys = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        int hash = key.hashCode();
        int index = (hash > 0 )? hash % length : -hash % length;
        if (buckets[index] != null) {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int hash = key.hashCode();
        int index = (hash > 0 )? hash % length : -hash % length;
        if (buckets[index] != null) {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    return node.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if ((float)size / length >= capacity) {
            resize();
        }
        int hash = key.hashCode();
        int index = (hash > 0 )? hash % length : -hash % length;
        if (buckets[index] == null) {
            buckets[index] = createBucket();
            buckets[index].add(createNode(key, value));
            Keys.add(key);
        } else if (get(key) == null) {
            buckets[index].add(createNode(key, value));
            Keys.add(key);
        } else {
            for (Node node : buckets[index]) {
                if (node.key.equals(key)) {
                    node.value = value;
                }
            }
            size--;
        }
        size++;
    }

    private void resize() {
        length *= 2;
        Collection<Node>[] temp = createTable(length);
        for (int i = 0; i < buckets.length / 2; i++) {
            if (buckets[i] != null) {
                for (Node node : buckets[i]) {
                    int hash = node.hashCode();
                    int index = hash % (length);
                    if (temp[index] == null) {
                        temp[index] = createBucket();
                        temp[index].add(node);
                        Keys.add(node.key);
                    } else {
                        temp[index].add(node);
                        Keys.add(node.key);
                    }
                }
            }
        }
        buckets = temp;
    }

    @Override
    public Set<K> keySet() {
        return Keys;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<K> {
        int index = 0;
        @Override
        public boolean hasNext() {
            return index < buckets.length;
        }

        @Override
        public K next() {
            return Keys.iterator().next();
        }
    }
}

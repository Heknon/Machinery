package me.oriharel.machinery.utilities;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Get random object from collection with weight
 *
 * @param <E> type of collection
 */
public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public boolean isPresent(double weight) {
        return map.containsKey(weight);
    }

    public E get(double weight) {
        return map.get(weight);
    }

    public void clear() {
        total = 0;
        map.clear();
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}

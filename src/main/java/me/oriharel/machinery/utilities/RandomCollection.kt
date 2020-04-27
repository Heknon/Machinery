package me.oriharel.machinery.utilities

import java.util.*

/**
 * Get random object from collection with weight
 *
 * @param <E> type of collection
</E> */
class RandomCollection<E> @JvmOverloads constructor(private val random: Random = Random()) {
    private val map: NavigableMap<Double, E> = TreeMap()
    private var total = 0.0
    fun add(weight: Double, result: E): RandomCollection<E> {
        if (weight <= 0) return this
        total += weight
        map[total] = result
        return this
    }

    fun isPresent(weight: Double): Boolean {
        return map.containsKey(weight)
    }

    operator fun get(weight: Double): E? {
        return map[weight]
    }

    fun clear() {
        total = 0.0
        map.clear()
    }

    operator fun next(): E {
        val value = random.nextDouble() * total
        return map.higherEntry(value).value
    }

}
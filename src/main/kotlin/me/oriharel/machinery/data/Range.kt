package me.oriharel.machinery.data

import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Range(private val start: Int, private val end: Int) {
    fun random(): Int {
        return ThreadLocalRandom.current().nextInt(start, end + 1)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val range = o as Range
        return start == range.start &&
                end == range.end
    }

    override fun hashCode(): Int {
        return Objects.hash(start, end)
    }

}
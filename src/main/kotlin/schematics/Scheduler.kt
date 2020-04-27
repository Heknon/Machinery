package schematics

import org.bukkit.Bukkit
import java.util.*

/**
 * Utility class that stores scheduler information.
 * @author SamB440
 */
class Scheduler {
    var task = 0
    var endAfter = 0
        private set
    var currentTicks = 0
        private set
    private var run: Runnable? = null
    private val data: MutableList<Any> = ArrayList()

    constructor() {}
    constructor(run: Runnable?) {
        this.run = run
    }

    fun setTask(task: Int): Scheduler {
        this.task = task
        return this
    }

    fun endAfter(ticks: Int): Scheduler {
        endAfter = ticks
        return this
    }

    fun getData(): List<Any> {
        return data
    }

    fun incrementTicks(amount: Int): Scheduler {
        currentTicks = currentTicks + amount
        if (currentTicks >= endAfter) {
            if (run != null) run!!.run()
            cancel()
        }
        return this
    }

    fun cancel() {
        if (run != null) run!!.run()
        Bukkit.getScheduler().cancelTask(task)
        data.clear()
    }
}
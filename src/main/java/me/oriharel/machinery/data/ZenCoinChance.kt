package me.oriharel.machinery.data

import me.oriharel.machinery.machine.MachineResourceGetProcess
import java.util.*

class ZenCoinChance(private val range: Range) : ChanceableOperation<Int?, MachineResourceGetProcess> {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ZenCoinChance
        return range == that.range
    }

    override fun hashCode(): Int {
        return Objects.hash(range)
    }

    override fun toString(): String {
        return "ZenCoinChance{" +
                "range=" + range +
                '}'
    }

    override fun getChanced(lootModifier: Double): Int? {
        return (range.random() * lootModifier).toInt()
    }

    override fun executeChanceOperation(machineResourceGetProcess: MachineResourceGetProcess, lootModifier: Double) {
        machineResourceGetProcess.addZenCoinsGained(getChanced(lootModifier)!!.toLong())
    }

}
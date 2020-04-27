package me.oriharel.machinery.resources

import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import me.oriharel.machinery.message.ChanceableOperation
import me.oriharel.machinery.resources.chance.Range
import java.util.*

class ZenCoinChance(private val range: Range) : ChanceableOperation<Int, MachineResourceGetProcess?> {
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

    override fun executeChanceOperation(param1: MachineResourceGetProcess?, lootModifier: Double) {
        param1?.addZenCoinsGained(getChanced(lootModifier)!!.toLong())
    }

}
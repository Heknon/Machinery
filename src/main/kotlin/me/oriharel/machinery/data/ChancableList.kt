package me.oriharel.machinery.data

import me.oriharel.machinery.machine.MachineResourceGetProcess
import java.util.*

class ChancableList<T : ChanceableOperation<*, MachineResourceGetProcess?>?> : ArrayList<T>(), ChanceableOperation<T, MachineResourceGetProcess> {
    override fun getChanced(lootModifier: Double): T? {
        return null
    }

    override fun executeChanceOperation(param1: MachineResourceGetProcess?, lootModifier: Double) {
        for (t in this) {
            t!!.executeChanceOperation(param1, lootModifier)
        }
    }
}
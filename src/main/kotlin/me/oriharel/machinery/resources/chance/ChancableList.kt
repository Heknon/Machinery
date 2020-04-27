package me.oriharel.machinery.resources.chance

import me.oriharel.machinery.machines.machine.MachineResourceGetProcess
import me.oriharel.machinery.message.ChanceableOperation
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
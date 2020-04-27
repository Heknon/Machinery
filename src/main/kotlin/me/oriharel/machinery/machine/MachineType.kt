package me.oriharel.machinery.machine

import me.oriharel.machinery.exceptions.NotMachineTypeException

enum class MachineType {
    LUMBERJACK, EXCAVATOR, MINER, FARMER, ALL;

    fun toTitle(): String {
        return when (this) {
            LUMBERJACK -> "Lumberjack"
            EXCAVATOR -> "Excavator"
            MINER -> "Miner"
            FARMER -> "Farmer"
            ALL -> "All Miner"
        }
    }

    companion object {
        @Throws(NotMachineTypeException::class)
        fun getMachine(machineType: String?): MachineType {
            if (machineType == null) throw NotMachineTypeException("You must supply a string which is not null")
            return when (machineType.toUpperCase()) {
                "LUMBERJACK" -> LUMBERJACK
                "EXCAVATOR" -> EXCAVATOR
                "MINER" -> MINER
                "FARMER" -> FARMER
                "ALL" -> ALL
                else -> throw NotMachineTypeException("The given machine type string is not a machine type. ($machineType)")
            }
        }
    }
}
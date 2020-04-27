package me.oriharel.machinery.machines.events

import me.oriharel.machinery.machines.machine.PlayerMachine
import org.bukkit.Location
import org.bukkit.event.HandlerList

class PostMachineBuildEvent(machine: PlayerMachine?, loc: Location) : MachineEvent<PlayerMachine?>(machine, loc) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}
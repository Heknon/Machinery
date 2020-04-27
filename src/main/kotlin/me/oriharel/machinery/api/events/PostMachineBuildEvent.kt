package me.oriharel.machinery.api.events

import me.oriharel.machinery.machine.PlayerMachine
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
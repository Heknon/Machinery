package me.oriharel.machinery.api.events

import me.oriharel.machinery.machine.Machine
import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class MachineEvent<T : Machine?>(val machine: T, val buildLocation: Location) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }

}
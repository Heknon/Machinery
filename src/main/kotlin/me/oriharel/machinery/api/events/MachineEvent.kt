package me.oriharel.machinery.api.events

import me.oriharel.machinery.machine.Machine
import org.bukkit.Location
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

open class MachineEvent<T : Machine?>(machine: T?, buildLocation: Location) : Event() {
    val machine: T
    val buildLocation: Location
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }

    init {
        this.machine = machine
        this.buildLocation = buildLocation
    }
}
package me.oriharel.machinery.machines.events

import me.oriharel.machinery.machines.machine.Machine
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList

class PreMachineBuildEvent(machine: Machine?, loc: Location) : MachineEvent<Machine?>(machine, loc), Cancellable {
    private var cancelled = false
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    companion object {
        val handlerList = HandlerList()
    }
}
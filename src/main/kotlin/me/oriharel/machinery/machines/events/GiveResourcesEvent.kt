package me.oriharel.machinery.machines.events

import me.oriharel.machinery.machines.machine.PlayerMachine
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Called when a player adds items to a resource inventory of a machine
 */
class GiveResourcesEvent(val resourceGiven: ItemStack, val machine: PlayerMachine, val openedInventory: Inventory) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }

}
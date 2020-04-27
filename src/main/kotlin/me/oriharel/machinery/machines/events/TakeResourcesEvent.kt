package me.oriharel.machinery.machines.events

import me.oriharel.machinery.machines.machine.PlayerMachine
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Called when a player takes items from a resource inventory of a machine
 */
class TakeResourcesEvent(val resourceTaken: ItemStack, val machine: PlayerMachine, val openedInventory: Inventory, val slotTakenFrom: Int) : Event() {

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }

}
package me.oriharel.machinery.inventory

import me.oriharel.machinery.Machinery
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class Listeners(private val machinery: Machinery) : Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.inventory.holder == null) return
        if (e.inventory.holder !is InventoryPage) return
        val inventory = e.inventory.holder as InventoryPage?
        val clicked = e.currentItem
        e.isCancelled = inventory!!.cancelClick
        if (clicked == null) return
        for (item in inventory.inventoryItems!!) {
            if (item!!.indexInInventory == e.slot) {
                if (item is InventoryNavigationItem) {
                    item.runOnClick()
                    item.navigate()
                } else {
                    item.runOnClick()
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.inventory.holder == null) return
        if (e.inventory.holder !is InventoryPage) return
        val inventory = e.inventory.holder as InventoryPage?
        if (inventory!!.onClose != null) inventory.onClose!!()
    }

}
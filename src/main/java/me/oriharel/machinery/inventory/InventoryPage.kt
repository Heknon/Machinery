package me.oriharel.machinery.inventory

import me.oriharel.machinery.machine.PlayerMachine
import me.oriharel.machinery.utilities.Callback
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.function.Consumer

open class InventoryPage : InventoryHolder {
    protected val size: Int
    protected val title: String?
    protected val fillment: InventoryItem?
    protected val inventory: Inventory
    var inventoryItems: Set<InventoryItem?>?
    var onClose: Callback?
    var cancelClick = true
    protected var owner: PlayerMachine

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, owner: PlayerMachine) {
        this.size = size
        this.title = title
        this.fillment = fillment
        this.inventoryItems = inventoryItems
        onClose = null
        this.owner = owner
        inventory = CraftInventoryCustom(this, size, title)
        inventory.maxStackSize = 20000000
        populateItems()
    }

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?,
                onClose: Callback?, owner: PlayerMachine) {
        this.size = size
        this.title = title
        this.fillment = fillment
        this.inventoryItems = inventoryItems
        this.onClose = onClose
        this.owner = owner
        inventory = CraftInventoryCustom(this, size, title)
        inventory.maxStackSize = 2000000000
        populateItems()
    }

    fun setInventoryItems(inventoryItems: Set<InventoryItem?>?): InventoryPage {
        this.inventoryItems = inventoryItems
        populateItems()
        return this
    }

    private fun populateItems() {
        if (inventoryItems == null || inventoryItems!!.isEmpty()) return
        val contents = inventory.contents
        inventoryItems!!.forEach(Consumer { item: InventoryItem? -> contents[item!!.indexInInventory] = item })
        for (i in contents.indices) {
            val item = contents[i]
            if ((item == null || item.type == Material.AIR) && fillment != null) {
                contents[i] = fillment.clone()
            }
        }
        inventory.contents = contents
    }

    fun setCancelClick(cancelClick: Boolean): InventoryPage {
        this.cancelClick = cancelClick
        return this
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
package me.oriharel.machinery.inventory

import me.oriharel.machinery.machines.machine.PlayerMachine
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCustom
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import java.util.function.Consumer

open class InventoryPage : InventoryHolder {
    private val size: Int
    private val title: String?
    private val fillment: InventoryItem?
    private val inventoryPage: Inventory
    var inventoryItems: Set<InventoryItem?>?
    var onClose: (() -> Unit)?
    var cancelClick = true
    private var owner: PlayerMachine

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?, owner: PlayerMachine) {
        this.size = size
        this.title = title
        this.fillment = fillment
        this.inventoryItems = inventoryItems
        onClose = null
        this.owner = owner
        inventoryPage = CraftInventoryCustom(this, size, title)
        inventoryPage.maxStackSize = 20000000
        populateItems()
    }

    constructor(size: Int, title: String?, fillment: InventoryItem?, inventoryItems: Set<InventoryItem?>?,
                onClose: () -> Unit, owner: PlayerMachine) {
        this.size = size
        this.title = title
        this.fillment = fillment
        this.inventoryItems = inventoryItems
        this.onClose = onClose
        this.owner = owner
        inventoryPage = CraftInventoryCustom(this, size, title)
        inventoryPage.maxStackSize = 2000000000
        populateItems()
    }

    fun setInventoryItems(inventoryItems: Set<InventoryItem?>?): InventoryPage {
        this.inventoryItems = inventoryItems
        populateItems()
        return this
    }

    private fun populateItems() {
        if (inventoryItems == null || inventoryItems!!.isEmpty()) return
        val contents = inventoryPage.contents
        inventoryItems!!.forEach(Consumer { item: InventoryItem? -> contents[item!!.indexInInventory] = item })
        for (i in contents.indices) {
            val item = contents[i]
            if ((item == null || item.type == Material.AIR) && fillment != null) {
                contents[i] = fillment.clone()
            }
        }
        inventoryPage.contents = contents
    }

    override fun getInventory(): Inventory {
        return inventoryPage
    }
}
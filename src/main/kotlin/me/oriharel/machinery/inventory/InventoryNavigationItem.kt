package me.oriharel.machinery.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

open class InventoryNavigationItem : InventoryItem {
    private var routeToName: String?
    var parentInventory: Inventory

    constructor(routeToName: String?, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?,
                vararg lore: String?) : super(indexInInventory, material, amount, displayName, *lore) {
        this.routeToName = routeToName
        this.parentInventory = parentInventory
    }

    constructor(routeToName: String?, parentInventory: Inventory, indexInInventory: Int, itemStack: ItemStack?) : super(indexInInventory, itemStack) {
        this.routeToName = routeToName
        this.parentInventory = parentInventory
    }

    constructor(routeToName: String?, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?) : super(indexInInventory, material, amount, displayName) {
        this.routeToName = routeToName
        this.parentInventory = parentInventory
    }

    open fun navigate(): InventoryNavigationItem {
        parentInventory.navigateToNamedRoute(routeToName)
        return this
    }
}
package me.oriharel.machinery.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Requires an InventoryPage that implements DatablePage
 * @param <T> type of data
</T> */
class InventoryNavigationItemData<S> : InventoryNavigationItem, NavigableData<S> {
    override var storedData: S
    private var routePage: NavigableDataInventoryPage<S>

    constructor(routePage: NavigableDataInventoryPage<S>, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?,
                navigationData: S, vararg lore: String?) : super(null, parentInventory, indexInInventory, material, amount, displayName, *lore) {
        storedData = navigationData
        this.routePage = routePage
    }

    constructor(routePage: NavigableDataInventoryPage<S>, parentInventory: Inventory, indexInInventory: Int, itemStack: ItemStack?, navigationData: S) : super(null, parentInventory, indexInInventory, itemStack) {
        storedData = navigationData
        this.routePage = routePage
    }

    constructor(routePage: NavigableDataInventoryPage<S>, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?,
                navigationData: S) : super(null, parentInventory, indexInInventory, material, amount, displayName) {
        storedData = navigationData
        this.routePage = routePage
    }

    override fun navigate(): InventoryNavigationItemData<S> {
        parentInventory.navigateDirect(routePage, storedData)
        return this
    }

    fun setStoredData(data: S): InventoryNavigationItemData<S> {
        storedData = data
        return this
    }
}
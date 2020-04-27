package me.oriharel.machinery.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Requires an InventoryPage that implements DatablePage
 * @param <T> type of data
</T> */
class InventoryNavigationItemData<T> : InventoryNavigationItem, NavigableData<T> {
    override var storedData: T
        private set
        public get() {
            return field
        }
    set
    private var routePage: NavigableDataInventoryPage<T>

    constructor(routePage: NavigableDataInventoryPage<T>, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?,
                navigationData: T, vararg lore: String?) : super(null, parentInventory, indexInInventory, material, amount, displayName, *lore) {
        storedData = navigationData
        this.routePage = routePage
    }

    constructor(routePage: NavigableDataInventoryPage<T>, parentInventory: Inventory, indexInInventory: Int, itemStack: ItemStack?, navigationData: T) : super(null, parentInventory, indexInInventory, itemStack) {
        storedData = navigationData
        this.routePage = routePage
    }

    constructor(routePage: NavigableDataInventoryPage<T>, parentInventory: Inventory, indexInInventory: Int, material: Material?, amount: Int, displayName: String?,
                navigationData: T) : super(null, parentInventory, indexInInventory, material, amount, displayName) {
        storedData = navigationData
        this.routePage = routePage
    }

    override fun navigate(): InventoryNavigationItemData<T> {
        parentInventory.navigateDirect(routePage, storedData)
        return this
    }

    override fun setStoredData(data: T): InventoryNavigationItemData<T> {
        storedData = data
        return this
    }
}
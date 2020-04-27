package me.oriharel.machinery.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class InventoryFillmentItem : InventoryItem {
    constructor(material: Material?, amount: Int, displayName: String?, vararg lore: String?) : super(-1, material, amount, displayName, *lore) {}
    constructor(itemStack: ItemStack?) : super(-1, itemStack)
    constructor(material: Material?, amount: Int, displayName: String?) : super(-1, material, amount, displayName) {}
}
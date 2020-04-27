package me.oriharel.machinery.inventory

import me.oriharel.machinery.utilities.NMS
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

open class InventoryItem : ItemStack {
    var indexInInventory: Int
    private var onClick: (() -> Unit)? = null

    constructor(indexInInventory: Int, material: Material?, amount: Int, displayName: String?, vararg lore: String?) : super(material!!, amount) {
        val meta = NMS.getItemStackMetaReference(this)
        meta!!.setDisplayName(displayName)
        meta.lore = Arrays.asList(*lore)
        this.indexInInventory = indexInInventory
    }

    constructor(indexInInventory: Int, itemStack: ItemStack?) : super(itemStack!!) {
        this.indexInInventory = indexInInventory
    }

    constructor(indexInInventory: Int, material: Material?, amount: Int, displayName: String?) : super(material!!, amount) {
        val meta = NMS.getItemStackMetaReference(this)
        meta!!.setDisplayName(displayName)
        this.indexInInventory = indexInInventory
    }

    fun runOnClick(): InventoryItem {
        if (onClick == null) return this
        onClick?.invoke()
        return this
    }

    fun setOnClick(onClick: (() -> Unit)): InventoryItem {
        this.onClick = onClick
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as InventoryItem
        return indexInInventory == that.indexInInventory &&
                onClick == that.onClick
    }

    fun equals(itemStack: ItemStack?, indexInInventory: Int): Boolean {
        return if (itemStack == null) false else itemStack == this && indexInInventory == this.indexInInventory
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), indexInInventory, onClick)
    }
}
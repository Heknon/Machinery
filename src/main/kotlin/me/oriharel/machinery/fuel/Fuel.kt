package me.oriharel.machinery.fuel

import me.oriharel.machinery.message.Message
import me.oriharel.machinery.message.Placeholder
import me.oriharel.machinery.utilities.NMS
import net.minecraft.server.v1_15_R1.NBTTagByte
import net.minecraft.server.v1_15_R1.NBTTagInt
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

class Fuel : ItemStack, Cloneable {
    var baseEnergy = 0
        private set
    private var lore: List<String>
    var energy: Int
        get() = baseEnergy * amount
        /**
         * sets the energy of a fuel.
         * applies new lore to show the amount of energy the fuel has.
         * sets a new NBT to hide away from the end user the amount of energy in the fuel and for easy access for the programmer
         * @param value energy to set to
         */
        set(value) {
            baseEnergy = value / amount
            val meta = itemMeta
            meta!!.lore = lore.stream().map { s: String -> Message(s, Placeholder("%amount%", baseEnergy)).appliedText }.collect(Collectors.toList())
            itemMeta = meta
            NMS.getItemStackUnhandledNBT(this)[ENERGY_NBT_KEY] = NBTTagInt.a(value)
        }

    constructor(material: Material?, energy: Int, amount: Int, displayName: String, lore: List<String>) : super(material!!, amount) {
        baseEnergy = energy * amount
        this.lore = lore
        val meta = ItemStack(material, 1).itemMeta
        meta!!.setDisplayName(Message(displayName).appliedText)
        meta.lore = lore.stream().map { s: String -> Message(s, Placeholder("%amount%", energy)).appliedText }.collect(Collectors.toList())
        itemMeta = meta
        NMS.getItemStackUnhandledNBT(this)[ENERGY_NBT_KEY] = NBTTagInt.a(energy)
        NMS.getItemStackUnhandledNBT(this)[FUEL_ITEM_NBT_IDENTIFIER] = NBTTagByte.a(true)
    }

    constructor(itemStack: ItemStack?, placeholderLore: List<String>) : super(itemStack!!) {
        lore = placeholderLore
        val compound = CraftItemStack.asNMSCopy(itemStack).tag
        baseEnergy = compound!!.getInt(ENERGY_NBT_KEY)
    }

    constructor(itemStack: ItemStack?, energy: Int, placeholderLore: List<String>) : super(itemStack!!) {
        lore = placeholderLore
        NMS.getItemStackUnhandledNBT(this)[ENERGY_NBT_KEY] = NBTTagInt.a(energy)
        NMS.getItemStackUnhandledNBT(this)[FUEL_ITEM_NBT_IDENTIFIER] = NBTTagByte.a(true)
    }

    override fun clone(): Fuel {
        val item: ItemStack = super<ItemStack>.clone()
        item.amount = amount
        return Fuel(item, baseEnergy, lore)
    }

    override fun toString(): String {
        return "Fuel{" +
                "energy=" + baseEnergy +
                '}'
    }

    companion object {
        private const val ENERGY_NBT_KEY = "fuel_energy"
        private const val FUEL_ITEM_NBT_IDENTIFIER = "machine_fuel"
    }
}
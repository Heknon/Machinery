package me.oriharel.machinery.fuel

import com.google.gson.Gson
import me.oriharel.machinery.Machinery
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class FuelManager(private val machinery: Machinery) {
    private val gson: Gson
    private val defaultType: Material?
    private val defaultLore: List<String>
    private val defaultDisplayName: String
    fun getFuel(amount: Int, energy: Int): Fuel {
        return Fuel(defaultType, energy, amount, defaultDisplayName, defaultLore)
    }

    fun getFuel(material: Material?, amount: Int, energy: Int): Fuel {
        return Fuel(material, energy, amount, defaultDisplayName, defaultLore)
    }

    fun getFuel(itemStack: ItemStack?): Fuel {
        return Fuel(itemStack, defaultLore)
    }

    fun getFuel(itemStack: ItemStack?, energy: Int): Fuel {
        return Fuel(itemStack, energy, defaultLore)
    }

    init {
        gson = Gson()
        val configLoad = machinery.fileManager.getConfig("fuel.yml").get()
        defaultType = Material.getMaterial(configLoad!!.getString("default_fuel_type")!!)
        defaultLore = configLoad.getStringList("default_lore")
        defaultDisplayName = configLoad.getString("default_display_name")!!
    }
}
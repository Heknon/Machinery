package me.oriharel.machinery.items

import com.google.gson.Gson
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.exceptions.MachineNotFoundException
import me.oriharel.machinery.machine.Machine
import me.oriharel.machinery.machine.MachineFactory
import me.oriharel.machinery.machine.PlayerMachine
import me.oriharel.machinery.utilities.NMS
import me.oriharel.machinery.utilities.Utils
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe
import net.minecraft.server.v1_15_R1.NBTTagCompound
import net.minecraft.server.v1_15_R1.NBTTagString
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.stream.Collectors

/**
 * Handles all the logic behind converting a machine to a item and an item to a machine
 */
class MachineItem {
    @Transient
    val recipe: CustomRecipe<*>?

    @Transient
    val machine: Machine?

    @Transient
    private val gson: Gson?

    constructor(recipe: CustomRecipe<*>?, machine: Machine, factory: MachineFactory?) {
        gson = Utils.getGsonSerializationBuilderInstance(machine.javaClass, factory)
        this.recipe = recipe
        this.machine = machine
    }

    constructor(itemStack: ItemStack?, factory: MachineFactory?, machineType: Class<out Machine>) {
        gson = Utils.getGsonSerializationBuilderInstance(machineType, factory)
        val `is` = CraftItemStack.asNMSCopy(itemStack)
        if (!`is`.hasTag() || !`is`.tag!!.hasKey("playerMachine") && !`is`.tag!!.hasKey("machine")) throw MachineNotFoundException("Machine not found in " +
                "ItemStack passed to MachineBlock constructor.")
        if (`is`.tag!!.hasKey("playerMachine")) {
            val data = `is`.tag!!.getString("playerMachine")
            machine = gson.fromJson(data, PlayerMachine::class.java)
            recipe = machine.getRecipe()
        } else if (`is`.tag!!.hasKey("machine")) {
            val data = `is`.tag!!.getString("machine")
            machine = gson.fromJson(data, Machine::class.java)
            recipe = machine.recipe
        } else {
            machine = null
            recipe = null
        }
    }

    // weird edge case. no clue why the hell I can't just return recipe.getResult()
    val itemStackWithAppliedPlaceholders: ItemStack?
        get() {
            if (machine!!.javaClass == PlayerMachine::class.java) {
                val configLoad: YamlConfiguration = Machinery.Companion.getInstance().getFileManager().getConfig("machines.yml").get()
                val `is` = ItemStack(recipe!!.result.type, 1)
                val meta = `is`.itemMeta
                meta!!.setDisplayName(applyPlaceholders(configLoad.getString(machine.machineName + ".deconstructedItem.displayName")!!))
                meta.lore = configLoad.getStringList(machine.machineName + ".deconstructedItem.lore")
                meta.lore = meta.lore!!.stream().map { string: String -> applyPlaceholders(string) }.collect(Collectors.toList())
                `is`.itemMeta = meta
                NMS.getItemStackUnhandledNBT(`is`)["playerMachine"] = NBTTagString.a(gson!!.toJson(machine, PlayerMachine::class.java))
                return `is`
            } else if (machine.javaClass == Machine::class.java) {
                // weird edge case. no clue why the hell I can't just return recipe.getResult()
                val amount = recipe!!.result.amount
                val clone = recipe.result.clone()
                clone.amount = amount
                val isNMS = CraftItemStack.asNMSCopy(clone)
                var tag = isNMS.tag
                if (tag == null) tag = NBTTagCompound()
                tag.remove("machine")
                tag["machine"] = NBTTagString.a(gson!!.toJson(machine, Machine::class.java))
                isNMS.tag = tag
                return CraftItemStack.asBukkitCopy(isNMS)
            }
            return null
        }

    private fun applyPlaceholders(string: String): String {
        var string = string
        string = string.replace("%total_resources_gained%".toRegex(), if (machine is PlayerMachine) machine.totalResourcesGained as Int else 0.toString())
        string = string.replace("%resources_gained%".toRegex(), if (machine is PlayerMachine) machine.resourcesGained.values.stream().mapToInt { obj: ItemStack? -> obj!!.amount }.sum() else 0.toString())
        string = string.replace("%total_zen_coins_gained%".toRegex(), if (machine is PlayerMachine) machine.totalZenCoinsGained as Int else 0.toString())
        string = string.replace("%zen_coins_gained%".toRegex(), if (machine is PlayerMachine) machine.zenCoinsGained as Int else 0.toString())
        string = string.replace("%energy%".toRegex(), if (machine is PlayerMachine) machine.energyInMachine else 0.toString())
        return string
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MachineItem
        return recipe == that.recipe &&
                machine == that.machine
    }

    override fun hashCode(): Int {
        return Objects.hash(recipe)
    }
}
package me.oriharel.machinery.machines.items

import com.google.gson.Gson
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.exceptions.MachineNotFoundException
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.machine.Machine
import me.oriharel.machinery.machines.machine.PlayerMachine
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
        val itemStackNMS = CraftItemStack.asNMSCopy(itemStack)
        if (!itemStackNMS.hasTag() || !itemStackNMS.tag!!.hasKey("playerMachine") && !itemStackNMS.tag!!.hasKey("machine")) throw MachineNotFoundException("Machine not found in " +
                "ItemStack passed to MachineBlock constructor.")
        when {
            itemStackNMS.tag!!.hasKey("playerMachine") -> {
                val data = itemStackNMS.tag!!.getString("playerMachine")
                machine = gson.fromJson(data, PlayerMachine::class.java)
                recipe = machine.recipe
            }
            itemStackNMS.tag!!.hasKey("machine") -> {
                val data = itemStackNMS.tag!!.getString("machine")
                machine = gson.fromJson(data, Machine::class.java)
                recipe = machine.recipe
            }
            else -> {
                machine = null
                recipe = null
            }
        }
    }

    // weird edge case. no clue why the hell I can't just return recipe.getResult()
    val itemStackWithAppliedPlaceholders: ItemStack?
        get() {
            if (machine!!.javaClass == PlayerMachine::class.java) {
                val configLoad: YamlConfiguration? = Machinery.instance?.fileManager?.getConfig("machines.yml")?.get()
                val itemStack = ItemStack(recipe!!.result.type, 1)
                val meta = itemStack.itemMeta
                meta!!.setDisplayName(applyPlaceholders(configLoad?.getString(machine.name + ".deconstructedItem.displayName")!!))
                meta.lore = configLoad.getStringList(machine.name + ".deconstructedItem.lore")
                meta.lore = meta.lore!!.stream().map { string: String -> applyPlaceholders(string) }.collect(Collectors.toList())
                itemStack.itemMeta = meta
                NMS.getItemStackUnhandledNBT(itemStack)["playerMachine"] = NBTTagString.a(gson!!.toJson(machine, PlayerMachine::class.java))
                return itemStack
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
        var str = string
        str = str.replace("%total_resources_gained%".toRegex(), if (machine is PlayerMachine) machine.totalResourcesGained.toInt().toString() else 0.toString())
        str = str.replace("%resources_gained%", if (machine is PlayerMachine) machine.resourcesGained?.values?.stream()?.mapToInt { obj: ItemStack? -> obj!!.amount }?.sum().toString() else 0.toString())
        str = str.replace("%total_zen_coins_gained%".toRegex(), if (machine is PlayerMachine) machine.totalZenCoinsGained.toInt().toString() else 0.toString())
        str = str.replace("%zen_coins_gained%".toRegex(), if (machine is PlayerMachine) machine.zenCoinsGained.toInt().toString() else 0.toString())
        str = str.replace("%energy%".toRegex(), if (machine is PlayerMachine) machine.energyInMachine.toString() else 0.toString())
        return str
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
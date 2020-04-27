package me.oriharel.machinery.utilities

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.oriharel.machinery.machines.machine.Machine
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.machine.PlayerMachine
import me.oriharel.machinery.machines.serializers.MachineTypeAdapter
import me.oriharel.machinery.machines.serializers.PlayerMachineTypeAdapter
import me.oriharel.machinery.message.Placeholder
import me.oriharel.machinery.serialization.*
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.upgrades.UpgradeType
import net.minecraft.server.v1_15_R1.NBTTagCompound
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat
import java.util.*
import javax.script.ScriptEngineManager

object Utils {
    val COMMA_NUMBER_FORMAT = DecimalFormat("#,###")
    private val SCRIPT_ENGINE = ScriptEngineManager().getEngineByName("JavaScript")

    /**
     * Compressed the X, Y, Z coordinates of a location to a long.
     * Compressed a location into 8 bytes
     * @param location location to compress
     * @return compressed location
     */
    fun locationToLong(location: Location?): Long {
        val x = location!!.blockX
        val y = location.blockY
        val z = location.blockZ
        return x.toLong() and 0x7FFFFFF or (z.toLong() and 0x7FFFFFF shl 27) or (y.toLong() shl 54)
    }

    /**
     * Decompresses a compressed location into an X, Y, Z only location
     * @param packed the compressed long type location
     * @return decompressed location
     */
    fun longToLocation(packed: Long): Location {
        val x = (packed shl 37 shr 37).toInt()
        val y = (packed ushr 54).toInt()
        val z = (packed shl 10 shr 37).toInt()
        return Location(null, x.toDouble(), y.toDouble(), z.toDouble())
    }

    /**
     * decompresses a location and sets it's world
     * @param packed the compressed long type location
     * @param world the world to set to
     * @return decompressed location
     */
    fun longToLocation(packed: Long, world: World?): Location {
        val loc = longToLocation(packed)
        loc.world = world
        return loc
    }

    /**
     * Checks if an inventory has enough space to add an item
     * @param inventory the inventory to check
     * @return true if it has enough space otherwise false
     */
    fun inventoryHasSpaceForItemAdd(inventory: Inventory): Boolean {
        return inventory.firstEmpty() != -1
    }

    fun <T> evaluateJavaScriptExpression(eval: String): T {
        return SCRIPT_ENGINE.eval(eval) as T
    }

    /**
     * Give an item or drop it on the floor near the player
     * @param player the player to give to
     * @param item the item to give
     * @param cloneDrop whether to clone it and give a clone or not
     * @param <T> generic type extending ItemStack. Included to support items such as Fuel
    </T> */
    fun <T : ItemStack?> giveItemOrDrop(player: HumanEntity, item: T, cloneDrop: Boolean) {
        val playerInventory: Inventory = player.inventory
        val playerLocation = player.location
        val playerWorld = player.world
        val amount = item!!.amount
        val toAdd: ItemStack = if (cloneDrop) item.clone() else item
        if (cloneDrop) toAdd.amount = amount
        if (inventoryHasSpaceForItemAdd(playerInventory)) {
            playerInventory.addItem(toAdd)
        } else {
            playerWorld.dropItemNaturally(playerLocation, toAdd)
        }
    }

    /**
     * Used codebase wide in the apparent need of changing the construction of the serializer.
     * If a type adapter needs to be added...
     * @param machineType machineType to make a serializer for
     * @param factory the machine factory
     * @param <T> the machine type extending base Machine class
     * @return Gson object for serializing a machine of machineType with
    </T> */
    fun <T : Machine?> getGsonSerializationBuilderInstance(machineType: Class<T>, factory: MachineFactory?): Gson {
        return GsonBuilder().registerTypeHierarchyAdapter(machineType, if (machineType == PlayerMachine::class.java) PlayerMachineTypeAdapter(factory) else MachineTypeAdapter<Machine>(factory)).registerTypeHierarchyAdapter(AbstractUpgrade::class.java,
                AbstractUpgradeTypeAdapter()).registerTypeHierarchyAdapter(Location::class.java, LocationTypeAdapter())
                .registerTypeHierarchyAdapter(NBTTagCompound::class.java, NBTTagCompoundTypeAdapter()).registerTypeHierarchyAdapter(UUID::class.java, UUIDTypeAdapter()).create()
    }

    /**
     * used to generate placeholders for use when sending config messages using Message object
     * @param location the location to generate placeholders for
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for location + extra placeholders given
     */
    fun getLocationPlaceholders(location: Location?, vararg extraPlaceholders: Placeholder): MutableList<Placeholder> {
        val placeholders: MutableList<Placeholder> = if (extraPlaceholders.isEmpty()) mutableListOf() else mutableListOf(*extraPlaceholders)

        placeholders.add(Placeholder("%x%", location!!.blockX))
        placeholders.add(Placeholder("%y%", location.blockY))
        placeholders.add(Placeholder("%z%", location.blockZ))
        placeholders.add(Placeholder("%world%", location.world!!.name))
        return placeholders
    }

    /**
     * helper function for creating placeholder locations using overloaded getLocationPlaceholders(Location location, Placeholder... extraPlaceholders)
     * wraps up a list into an array
     * @param location the location to generate placeholders for
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for location + extra placeholders given
     */
    fun getLocationPlaceholders(location: Location?, extraPlaceholders: List<Placeholder>): MutableList<Placeholder> {
        return getLocationPlaceholders(location, *extraPlaceholders.toTypedArray())
    }

    /**
     * used to wrap up creation of placeholder array from list
     * @param machine the location to generate placeholders for
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for machine + extra placeholders given
     */
    fun getMachinePlaceholders(machine: PlayerMachine, extraPlaceholders: List<Placeholder>): MutableList<Placeholder> {
        return getMachinePlaceholders(machine, *extraPlaceholders.toTypedArray())
    }

    /**
     * used to wrap up creation of placeholder array from list
     * @param amount the amount placeholder value
     * @param thing the thing placeholder value
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for amount thing + extra placeholders given
     */
    fun getAmountThingPlaceholder(amount: Int, thing: String?, extraPlaceholders: List<Placeholder>): MutableList<Placeholder> {
        return getAmountThingPlaceholder(amount, thing, *extraPlaceholders.toTypedArray())
    }

    /**
     * used to generate placeholders for use when sending config messages using Message object
     * @param machine the location to generate placeholders for
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for machine + extra placeholders given
     */
    fun getMachinePlaceholders(machine: PlayerMachine, vararg extraPlaceholders: Placeholder): MutableList<Placeholder> {
        val placeholders: MutableList<Placeholder> = if (extraPlaceholders.isEmpty()) mutableListOf() else mutableListOf(*extraPlaceholders)
        placeholders.add(Placeholder("%machine_nane%", machine.name))
        placeholders.add(Placeholder("%machine_energy%", machine.energyInMachine))
        placeholders.add(Placeholder("%machine_max_fuel%", machine.maxFuel))
        placeholders.add(Placeholder("%machine_fuel_deficiency%", machine.fuelDeficiency))
        placeholders.add(Placeholder("%machine_total_zen_coins_gained%", machine.totalZenCoinsGained.toInt()))
        placeholders.add(Placeholder("%machine_zen_coins_gained%", machine.zenCoinsGained.toInt()))
        placeholders.add(Placeholder("%machine_total_resources_gained%", machine.totalResourcesGained.toInt()))
        placeholders.add(Placeholder("%machine_resources_gained%", machine.resourcesGained?.values?.stream()?.mapToInt { obj: ItemStack? -> obj!!.amount }?.sum()))
        placeholders.add(Placeholder("%upgrade_loot_bonus_name%",
                machine.upgrades?.stream()?.filter { u: AbstractUpgrade? -> u?.upgradeType == UpgradeType.LOOT_BONUS }?.findAny()?.get()?.upgradeName))
        placeholders.add(Placeholder("%upgrade_loot_bonus_level%",
                machine.upgrades?.stream()?.filter { u: AbstractUpgrade? -> u?.upgradeType == UpgradeType.LOOT_BONUS }?.findAny()?.get()?.level))
        placeholders.add(Placeholder("%upgrade_speed_name%",
                machine.upgrades?.stream()?.filter { u: AbstractUpgrade? -> u?.upgradeType == UpgradeType.SPEED }?.findAny()?.get()?.upgradeName))
        placeholders.add(Placeholder("%upgrade_speed_level%",
                machine.upgrades?.stream()?.filter { u: AbstractUpgrade? -> u?.upgradeType == UpgradeType.SPEED }?.findAny()?.get()?.level))
        return placeholders
    }

    /**
     * Used to represent something represented as an amount of a thing
     * @param amount amount of thing
     * @param thing thing
     * @param extraPlaceholders other placeholders that may be added upon the ones generated
     * @return the generated placeholder for amount thing + extra placeholders given
     */
    fun getAmountThingPlaceholder(amount: Int, thing: String?, vararg extraPlaceholders: Placeholder): MutableList<Placeholder> {
        val placeholders: MutableList<Placeholder> = if (extraPlaceholders.isEmpty()) mutableListOf() else mutableListOf(*extraPlaceholders)
        placeholders.add(Placeholder("%amount%", amount))
        placeholders.add(Placeholder("%thing%", thing))
        return placeholders
    }
}
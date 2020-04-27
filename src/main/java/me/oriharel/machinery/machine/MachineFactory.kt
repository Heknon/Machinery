package me.oriharel.machinery.machine

import me.oriharel.machinery.Machinery
import me.oriharel.machinery.exceptions.MachineNotFoundException
import me.oriharel.machinery.exceptions.MaterialNotFoundException
import me.oriharel.machinery.exceptions.NotMachineTypeException
import me.oriharel.machinery.exceptions.RecipeNotFoundException
import me.oriharel.machinery.structure.Structure
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.oriharel.machinery.utilities.NMS
import me.oriharel.machinery.utilities.Utils
import me.wolfyscript.customcrafting.CustomCrafting
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe
import net.minecraft.server.v1_15_R1.NBTTagString
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*

class MachineFactory(val machinery: Machinery) {

    /**
     * Create a new machine it's config name
     *
     * @param machineKey config name in machines.yml
     * @return Machine representing the config data
     * @throws IllegalArgumentException  machine key is null
     * @throws MachineNotFoundException  machine not found in config
     * @throws MaterialNotFoundException machine core type is not a valid type
     * @throws RecipeNotFoundException   recipe made for machine is not found
     * @throws NotMachineTypeException   the machine type given is not a real machine type {@see MachineType}
     */
    @Throws(IllegalArgumentException::class, MachineNotFoundException::class, MaterialNotFoundException::class, RecipeNotFoundException::class, NotMachineTypeException::class)
    fun createMachine(machineKey: String?): Machine? {
        requireNotNull(machineKey) { "Machine machineKey must not be null (MachineFactory)" }
        val configLoad = machinery.fileManager.getConfig("machines.yml").get()
        if (!configLoad!!.isConfigurationSection(machineKey)) throw MachineNotFoundException("The machine with the key of \"" + machineKey + "\" was not found in " +
                "machines.yml.")
        val section = machinery.fileManager.getConfig("machines.yml").get().getConfigurationSection(machineKey)!!
        val machineCoreBlockTypeString = section.getString("open_gui_block_type")
                ?: throw MaterialNotFoundException("You must give a open_gui_block_type in the machine section of machine $machineKey")
        val machineCoreBlockType = Material.getMaterial(machineCoreBlockTypeString)
                ?: throw MaterialNotFoundException("No material named \"$machineCoreBlockTypeString\" was found.")
        val maxFuel = section.getInt("max_fuel", 0)
        val fuelDeficiency = section.getInt("deficiency", 0)
        var recipeName = section.getString("recipe", null)
                ?: throw NullPointerException("No recipe given in machine section for machine named $machineKey")
        recipeName = recipeName.replace("\\|".toRegex(), ":")
        val recipe = CustomCrafting.getRecipeHandler().getRecipe(recipeName)
                ?: throw RecipeNotFoundException("Recipe with the name " + recipeName + " given in the machine section of " + machineKey + " has not been " +
                        "located.")
        val machineType: MachineType = MachineType.Companion.getMachine(section.getString("type", null))
        val structure = machinery.structureManager.getSchematicByPath(File(machinery.dataFolder, "structures/" + machineKey + Machinery.Companion.STRUCTURE_EXTENSION).path)
        return createMachine(maxFuel, fuelDeficiency, machineType, structure, recipe, machineKey, machineCoreBlockType)
    }

    /**
     * Create a machine with already known values
     *
     * @param maxFuel              the max fuel a machine can hold
     * @param fuelDeficiency       the fuel deficiency. the amount of fuel removed every resource get cycle
     * @param machineType          the type of the machine
     * @param structure            the build structure of the machine
     * @param recipe               the recipe used to make the machine
     * @param machineKey           the key of the machine in config aka machine name
     * @param machineCoreBlockType the core block type later used to figure out, when building the machine, where the core block is
     * @return Machine based on values given
     * @throws IllegalArgumentException type give is null
     */
    @Throws(IllegalArgumentException::class)
    fun createMachine(maxFuel: Int,
                      fuelDeficiency: Int,
                      machineType: MachineType?,
                      structure: Structure?,
                      recipe: CustomRecipe<*>?,
                      machineKey: String?, machineCoreBlockType: Material?): Machine? {
        requireNotNull(machineType) { "Machine type must not be null (MachineFactory)" }
        val machine = Machine(maxFuel, fuelDeficiency, machineType, structure,
                recipe, machineKey, machineCoreBlockType, this)
        machine.setRecipe(injectMachineNBTIntoRecipe(machine.getRecipe(), machine))
        return machine
    }

    @Throws(IllegalArgumentException::class)
    fun createMachine(maxFuel: Int,
                      fuelDeficiency: Int,
                      machineType: MachineType?,
                      structure: Structure?,
                      recipe: CustomRecipe<*>?,
                      machineKey: String?, machineCoreBlockType: Material?, totalResourcesGained: Double,
                      resourcesGained: HashMap<Material?, ItemStack?>?,
                      energyInMachine: Int, machineCoreBlockLocation: Location?, zenCoinsGained: Double, totalZenCoinsGained: Double, owner: UUID?,
                      upgrades: List<AbstractUpgrade?>?, playersWithAccessPermission: Set<UUID?>?): PlayerMachine? {
        requireNotNull(machineType) { "Machine type must not be null (MachineFactory)" }
        return PlayerMachine(maxFuel, fuelDeficiency, machineType, structure,
                recipe, machineKey, machineCoreBlockType, playersWithAccessPermission, totalResourcesGained, resourcesGained, energyInMachine, machineCoreBlockLocation,
                zenCoinsGained, totalZenCoinsGained, owner, upgrades, this)
    }

    @Throws(IllegalArgumentException::class)
    fun createMachine(machine: Machine?, machineCoreBlockLocation: Location?, totalResourcesGained: Double,
                      energyInMachine: Int, zenCoinsGained: Double, totalZenCoinsGained: Double, owner: UUID?,
                      upgrades: List<AbstractUpgrade?>?, resourcesGained: HashMap<Material?, ItemStack?>?, playersWithAccessPermission: Set<UUID?>?): PlayerMachine? {
        requireNotNull(machine) { "Machine must not be null (MachineFactory)" }
        return createMachine(machine.maxFuel, machine.fuelDeficiency, machine.machineType, machine.structure, machine.recipe, machine.machineName,
                machine.machineCoreBlockType, totalResourcesGained, resourcesGained, energyInMachine, machineCoreBlockLocation, zenCoinsGained, totalZenCoinsGained,
                owner, upgrades, playersWithAccessPermission)
    }

    /**
     * Inject machine data into a recipe
     * used so that when crafting a recipe you will have inside the NBT (data) of the itemstack a serialized machine so that later on you could build it
     * @param recipe the recipe to inject into
     * @param machine the machine to inject into the recipe
     * @return the newly injected recipe
     */
    private fun injectMachineNBTIntoRecipe(recipe: CustomRecipe<*>?, machine: Machine): CustomRecipe<*>? {
        val results = recipe!!.customResults
        for (item in results) {
            NMS.getItemStackUnhandledNBT(item)["machine"] = NBTTagString.a(Utils.getGsonSerializationBuilderInstance(PlayerMachine::class.java, this).toJson(machine,
                    Machine::class.java))
        }
        return recipe
    }

}
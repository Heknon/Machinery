package me.oriharel.machinery.machine

import com.google.gson.annotations.JsonAdapter
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.items.MachineItem
import me.oriharel.machinery.serialization.AbstractUpgradeTypeAdapter
import me.oriharel.machinery.structure.Structure
import me.oriharel.machinery.upgrades.AbstractUpgrade
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerMachine(maxFuel: Int, fuelDeficiency: Int, machineType: MachineType,
                    structure: Structure?, recipe: CustomRecipe<*>?, machineName: String?, openGUIBlockType: Material?,
                    val playersWithAccessPermission: Set<UUID?>?, var totalResourcesGained: Double, var resourcesGained: HashMap<Material?, ItemStack?>?, var energyInMachine: Int, val machineCore: Location?, var zenCoinsGained: Double,
                    var totalZenCoinsGained: Double, val owner: UUID?, @field:JsonAdapter(AbstractUpgradeTypeAdapter::class) val upgrades: List<AbstractUpgrade?>?, @field:Transient val factory: MachineFactory) : Machine(maxFuel, fuelDeficiency, machineType, structure, recipe, machineName, openGUIBlockType, factory) {

    @Transient
    private var machineResourceGetProcess: MachineResourceGetProcess? = null

    /**
     * Clears all TileEntity block data from core block of machine
     * gets all locations of the blocks that make up the machine
     * unregisters the machine from the plugin
     * stops mining process
     * removes all machine related blocks
     *
     * @return MachineBlock that represents the deconstructed machine and the data in it
     */
    fun deconstruct(): MachineItem {
        val machineManager: MachineManager = Machinery.Companion.getInstance().getMachineManager()
        machineManager.clearMachineTileStateDataFromBlock(machineCore!!.block)
        val machinePartLocations = machineManager.getPlayerMachineLocations(machineCore.block)
        machineManager.unregisterPlayerMachine(this)
        machineResourceGetProcess!!.endProcess()
        for (loc in machinePartLocations!!) {
            val block = loc!!.block
            block.type = Material.AIR
        }
        return MachineItem(recipe, this, factory)
    }

    val minerProcess: MachineResourceGetProcess
        get() {
            if (machineResourceGetProcess == null) machineResourceGetProcess = MachineResourceGetProcess(this)
            return machineResourceGetProcess!!
        }

    fun addEnergy(energy: Int) {
        energyInMachine += energy
    }

    fun removeEnergy(energy: Int) {
        energyInMachine -= energy
    }

    fun addZenCoinsGained(zenCoinsGained: Double) {
        totalZenCoinsGained += zenCoinsGained
        this.zenCoinsGained += zenCoinsGained
    }

    fun removeZenCoinsGained(zenCoinsToRemove: Double) {
        zenCoinsGained -= zenCoinsToRemove
    }

    override fun toString(): String {
        return "PlayerMachine{" +
                "machineCore=" + machineCore +
                ", owner=" + owner +
                ", playersWithAccessPermission=" + playersWithAccessPermission +
                ", upgrades=" + upgrades +
                ", totalResourcesGained=" + totalResourcesGained +
                ", resourcesGained=" + resourcesGained +
                ", energyInMachine=" + energyInMachine +
                ", totalZenCoinsGained=" + totalZenCoinsGained +
                ", zenCoinsGained=" + zenCoinsGained +
                ", machineResourceGetProcess=" + machineResourceGetProcess +
                '}'
    }

}
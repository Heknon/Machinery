package me.oriharel.machinery.machine

import com.google.gson.annotations.JsonAdapter
import me.oriharel.machinery.items.MachineItem
import me.oriharel.machinery.serialization.MachineTypeAdapter
import me.oriharel.machinery.structure.Structure
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe
import org.bukkit.Material

@JsonAdapter(MachineTypeAdapter::class)
open class Machine(
        var maxFuel: Int,
        var fuelDeficiency: Int,
        val type: MachineType,
        val structure: Structure?,
        var recipe: CustomRecipe<*>?,
        val machineName: String?, val machineCoreBlockType: Material?, factory: MachineFactory?) {
    private val machineItem: MachineItem = MachineItem(recipe, this, factory)

    override fun toString(): String {
        return "Machine{" +
                "machineType=" + type +
                ", structure=" + structure +
                ", machineName='" + machineName + '\'' +
                ", machineBlock=" + machineItem +
                ", machineCoreBlockType=" + machineCoreBlockType +
                ", recipe=" + recipe +
                ", fuelDeficiency=" + fuelDeficiency +
                ", maxFuel=" + maxFuel +
                '}'
    }

}
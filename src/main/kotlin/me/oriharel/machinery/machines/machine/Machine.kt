package me.oriharel.machinery.machines.machine

import com.google.gson.annotations.JsonAdapter
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.items.MachineItem
import me.oriharel.machinery.machines.serializers.MachineTypeAdapter
import me.oriharel.machinery.structure.Structure
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe
import org.bukkit.Material

@JsonAdapter(MachineTypeAdapter::class)
open class Machine(
        var maxFuel: Int,
        var fuelDeficiency: Int,
        val structure: Structure?,
        var recipe: CustomRecipe<*>?,
        val name: String?, val coreBlockType: Material?, factory: MachineFactory?) {
    private val item: MachineItem = MachineItem(recipe, this, factory)

    override fun toString(): String {
        return "Machine{" +
                "structure=" + structure +
                ", name='" + name + '\'' +
                ", item=" + item +
                ", machineCoreBlockType=" + coreBlockType +
                ", recipe=" + recipe +
                ", fuelDeficiency=" + fuelDeficiency +
                ", maxFuel=" + maxFuel +
                '}'
    }

}
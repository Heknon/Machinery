package me.oriharel.machinery.machines.serializers

import com.google.gson.*
import me.oriharel.machinery.Machinery
import me.oriharel.machinery.machines.MachineFactory
import me.oriharel.machinery.machines.machine.Machine
import me.oriharel.machinery.structure.Structure
import me.wolfyscript.customcrafting.CustomCrafting
import org.bukkit.Material
import java.io.File
import java.lang.reflect.Type

open class MachineTypeAdapter<T : Machine?>
(protected var factory: MachineFactory?) : JsonSerializer<T>, JsonDeserializer<T?> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): T? {
        return getDeserializedMachine(jsonElement.asJsonObject, jsonDeserializationContext)
    }

    override fun serialize(machine: T, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return getSerializedMachine(machine, jsonSerializationContext)
    }

    protected open fun getDeserializedMachine(machineJsonObject: JsonObject, context: JsonDeserializationContext): T? {
        val name = machineJsonObject["name"].asString
        val recipeName = machineJsonObject["recipe"].asString
        val maxFuel = machineJsonObject["maxFuel"].asInt
        val fuelDeficiency = machineJsonObject["fuelDeficiency"].asInt
        val coreBlockType = Material.getMaterial(machineJsonObject["coreBlockType"].asString)
        val structure: Structure? = Machinery.instance?.structureManager?.getSchematicByPath(machineJsonObject["structure"].asString)
        val recipe = CustomCrafting.getRecipeHandler().getRecipe(recipeName)
        if (factory == null) factory = Machinery.instance?.machineManager?.machineFactory

        return factory!!.createMachine(maxFuel,
                fuelDeficiency, structure, recipe, name, coreBlockType) as T?
    }

    protected open fun getSerializedMachine(machine: T, context: JsonSerializationContext): JsonObject {
        val obj = JsonObject()
        obj.add("name", JsonPrimitive(machine?.name))
        obj.add("structure", JsonPrimitive(getSchematicPath(machine?.structure)))
        obj.add("recipe", JsonPrimitive(machine?.recipe?.id))
        obj.add("fuelDeficiency", JsonPrimitive(machine?.fuelDeficiency))
        obj.add("maxFuel", JsonPrimitive(machine?.maxFuel))
        obj.add("coreBlockType", JsonPrimitive(machine?.coreBlockType.toString()))
        return obj
    }

    private fun getSchematicPath(struct: Structure?): String {
        var schematic: String
        val structure = struct?.schematic
        try {
            val a = structure!!.javaClass.getDeclaredField("schematic")
            a.isAccessible = true
            schematic = (a[structure] as File).path
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            schematic = ""
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            schematic = ""
        }
        return schematic
    }

}
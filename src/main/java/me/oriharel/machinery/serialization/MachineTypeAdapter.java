package me.oriharel.machinery.serialization;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.structure.Structure;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.Material;
import schematics.Schematic;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class MachineTypeAdapter<T extends Machine> implements JsonSerializer<T>, JsonDeserializer<T> {
    protected MachineFactory factory;

    public MachineTypeAdapter(MachineFactory factory) {
        //Preconditions.checkNotNull(factory, "Factory must not be null!");

        this.factory = factory;
    }

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return getDeserializedMachine(jsonElement.getAsJsonObject(), jsonDeserializationContext);
    }

    @Override
    public JsonElement serialize(T machine, Type type, JsonSerializationContext jsonSerializationContext) {
        return getSerializedMachine(machine, jsonSerializationContext);
    }

    protected T getDeserializedMachine(JsonObject machineJsonObject, JsonDeserializationContext context) {

        String machineName = machineJsonObject.get("name").getAsString();
        String recipeName = machineJsonObject.get("recipe").getAsString();


        int machineReach = machineJsonObject.get("reach").getAsInt();
        int machineMaxFuel = machineJsonObject.get("maxFuel").getAsInt();
        int machineFuelDeficiency = machineJsonObject.get("fuelDeficiency").getAsInt();

        List<String> fuelTypes = context.deserialize(machineJsonObject.get("fuelTypes"), List.class);

        MachineType machineType = MachineType.valueOf(machineJsonObject.get("type").getAsString());
        Material machineCoreBlockType = Material.getMaterial(machineJsonObject.get("coreBlockType").getAsString());

        Structure structure =
                Machinery.getInstance().getStructureManager().getSchematicByPath(machineJsonObject.get("structure").getAsString());

        CustomRecipe<?> recipe = CustomCrafting.getRecipeHandler().getRecipe(recipeName);

        if (factory == null) factory = Machinery.getInstance().getMachineManager().getMachineFactory();
        return (T) factory.createMachine(machineReach, machineMaxFuel,
                machineFuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, machineCoreBlockType);
    }

    protected JsonObject getSerializedMachine(T machine, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.add("name", new JsonPrimitive(machine.getMachineName()));
        obj.add("type", new JsonPrimitive(machine.getType().toString()));
        obj.add("structure", new JsonPrimitive(getSchematicPath(machine.getStructure())));
        obj.add("recipe", new JsonPrimitive(machine.getRecipe().getId()));
        obj.add("fuelDeficiency", new JsonPrimitive(machine.getFuelDeficiency()));
        obj.add("reach", new JsonPrimitive(machine.getMachineReach()));
        obj.add("maxFuel", new JsonPrimitive(machine.getMaxFuel()));
        obj.add("coreBlockType", new JsonPrimitive(machine.getMachineCoreBlockType().toString()));
        obj.add("fuelTypes", context.serialize(machine.getFuelTypes(), List.class));

        return obj;
    }

    private String getSchematicPath(Structure struct) {
        String schematic;

        Schematic structure = struct.getSchematic();

        try {
            Field a = structure.getClass().getDeclaredField("schematic");
            a.setAccessible(true);
            schematic = ((File) a.get(structure)).getPath();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            schematic = "";
        }
        return schematic;
    }
}

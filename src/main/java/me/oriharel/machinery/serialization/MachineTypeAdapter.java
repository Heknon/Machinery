package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.structure.Structure;
import net.islandearth.schematics.extended.Schematic;
import org.bukkit.Material;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class MachineTypeAdapter implements JsonSerializer<Machine>, JsonDeserializer<Machine> {
    private MachineFactory factory;

    public MachineTypeAdapter(MachineFactory factory) {
        this.factory = factory;
    }

    @Override
    public Machine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String machineName = obj.get("machineName").getAsString();
        MachineType machineType = MachineType.valueOf(obj.get("machineType").getAsString());
        Structure structure =
                Machinery.getInstance().getStructureManager().getSchematicByPath(obj.get("structure").getAsString());
        int speed = obj.get("machineSpeed").getAsInt();
        Material referenceBlockMaterial = Material.getMaterial(obj.get("machineReferenceBlock").getAsString());
        Material openGUIBlockType = Material.getMaterial(obj.get("machineOpenGUIBlockType").getAsString());
        String recipeName = obj.get("machineRecipe").getAsString();
        Recipe recipe =
                CustomRecipesAPI.getImplementation().getRecipesManager().getRecipes().stream().filter(r -> r.getRecipeKey().equalsIgnoreCase(recipeName)).findAny().orElse(null);
        int machineFuelDeficiency = obj.get("machineFuelDeficiency").getAsInt();
        List<String> fuelTypes = jsonDeserializationContext.deserialize(obj.get("machineFuelTypes"), List.class);
        int machineReach = obj.get("machineReach").getAsInt();
        int machineMaxFuel = obj.get("machineMaxFuel").getAsInt();
        return factory.createMachine(referenceBlockMaterial, machineReach, speed, machineMaxFuel,
                machineFuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType);
    }

    @Override
    public JsonElement serialize(Machine machine, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        Structure struct = machine.getStructure();
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
        obj.add("machineName", new JsonPrimitive(machine.getMachineName()));
        obj.add("machineType", new JsonPrimitive(machine.getType().toString()));
        obj.add("structure", new JsonPrimitive(schematic));
        obj.add("machineSpeed", new JsonPrimitive(machine.getSpeed()));
        obj.add("machineReferenceBlock", new JsonPrimitive(machine.getReferenceBlockType().toString()));
        obj.add("machineRecipe", new JsonPrimitive(machine.getRecipe().getRecipeKey()));
        obj.add("machineFuelDeficiency", new JsonPrimitive(machine.getFuelDeficiency()));
        obj.add("machineFuelTypes", jsonSerializationContext.serialize(machine.getFuelTypes(), List.class));
        obj.add("machineReach", new JsonPrimitive(machine.getMachineReach()));
        obj.add("machineMaxFuel", new JsonPrimitive(machine.getMaxFuel()));
        obj.add("machineOpenGUIBlockType", new JsonPrimitive(machine.getOpenGUIBlockType().toString()));
        return obj;
    }
}

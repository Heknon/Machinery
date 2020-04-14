package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.machine.MachineFactory;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.structure.Structure;
import net.islandearth.schematics.extended.Schematic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class PlayerMachineTypeAdapter implements JsonSerializer<PlayerMachine>, JsonDeserializer<PlayerMachine> {
    private MachineFactory factory;

    public PlayerMachineTypeAdapter(MachineFactory factory) {
        this.factory = factory;
    }

    @Override
    public PlayerMachine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String machineName = obj.get("machineName").getAsString();
        MachineType machineType = MachineType.valueOf(obj.get("machineType").getAsString());
        double machineTotalResourcesGained = obj.get("machineTotalResourcesGained").getAsDouble();
        double machineTotalZenCoinsGained = obj.get("machineTotalZenCoinsGained").getAsDouble();
        double machineZenCoinsGained = obj.get("machineZenCoinsGained").getAsDouble();
        List<ItemStack> machineResourcesGained = jsonDeserializationContext.deserialize(obj.get("machineResourcesGained"), List.class);
        Structure structure = Machinery.getInstance().getStructureManager().getSchematicByPath(obj.get("structure").getAsString());
        int speed = obj.get("machineSpeed").getAsInt();
        Material referenceBlockMaterial = Material.getMaterial(obj.get("machineReferenceBlock").getAsString());
        String recipeName = obj.get("machineRecipe").getAsString();
        Material openGUIBlockType = Material.getMaterial(obj.get("machineOpenGUIBlockType").getAsString());
        Recipe recipe =
                CustomRecipesAPI.getImplementation().getRecipesManager().getRecipes().stream().filter(r -> r.getRecipeKey().equalsIgnoreCase(recipeName)).findAny().orElse(null);
        List<PlayerFuel> fuel = jsonDeserializationContext.deserialize(obj.get("machineFuel"), List.class);
        Location referenceBlockLocation = jsonDeserializationContext.deserialize(obj.get("machineReferenceBlockLocation"), Location.class);
        Location openGUIBlockLocation = jsonDeserializationContext.deserialize(obj.get("machineOpenGUIBlockLocation"), Location.class);
        int machineFuelDeficiency = obj.get("machineFuelDeficiency").getAsInt();
        List<String> fuelTypes = jsonDeserializationContext.deserialize(obj.get("machineFuelTypes"), List.class);
        UUID owner = new UUID(obj.get("machineOwnerMost").getAsLong(), obj.get("machineOwnerLeast").getAsLong());
        int machineReach = obj.get("machineReach").getAsInt();
        int machineMaxFuel = obj.get("machineMaxFuel").getAsInt();
        return factory.createMachine(referenceBlockMaterial, machineReach, speed, machineMaxFuel,
                machineFuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType, referenceBlockLocation,
                machineTotalResourcesGained,
                machineResourcesGained, fuel, openGUIBlockLocation, machineZenCoinsGained, machineTotalZenCoinsGained, owner);
    }

    @Override
    public JsonElement serialize(PlayerMachine machine, Type type, JsonSerializationContext jsonSerializationContext) {
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
        obj.add("machineFuelTypes", jsonSerializationContext.serialize(machine.getFuelTypes()));
        obj.add("machineReach", new JsonPrimitive(machine.getMachineReach()));
        obj.add("machineMaxFuel", new JsonPrimitive(machine.getMaxFuel()));
        obj.add("machineOpenGUIBlockType", new JsonPrimitive(machine.getOpenGUIBlockType().toString()));
        obj.add("machineTotalResourcesGained", new JsonPrimitive(machine.getTotalResourcesGained()));
        obj.add("machineResourcesGained", jsonSerializationContext.serialize(machine.getResourcesGained(), List.class));
        obj.add("machineZenCoinsGained", new JsonPrimitive(machine.getZenCoinsGained()));
        obj.add("machineTotalZenCoinsGained", new JsonPrimitive(machine.getTotalZenCoinsGained()));
        obj.add("machineReferenceBlockLocation", jsonSerializationContext.serialize(machine.getReferenceBlockLocation(), Location.class));
        obj.add("machineOpenGUIBlockLocation", jsonSerializationContext.serialize(machine.getOpenGUIBlockLocation(), Location.class));
        obj.add("machineOwnerLeast", new JsonPrimitive(machine.getOwner().getLeastSignificantBits()));
        obj.add("machineOwnerMost", new JsonPrimitive(machine.getOwner().getMostSignificantBits()));
        obj.add("machineFuel", jsonSerializationContext.serialize(machine.getFuels(), List.class));
        return obj;
    }
}

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
import me.oriharel.machinery.upgrades.AbstractUpgrade;
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
        String machineName = obj.get("Name").getAsString();
        MachineType machineType = MachineType.valueOf(obj.get("Type").getAsString());
        double machineTotalResourcesGained = obj.get("TotalResourcesGained").getAsDouble();
        double machineTotalZenCoinsGained = obj.get("TotalZenCoinsGained").getAsDouble();
        double machineZenCoinsGained = obj.get("ZenCoinsGained").getAsDouble();
        List<ItemStack> machineResourcesGained = jsonDeserializationContext.deserialize(obj.get("ResourcesGained"), List.class);
        Structure structure = Machinery.getInstance().getStructureManager().getSchematicByPath(obj.get("structure").getAsString());
        Material referenceBlockMaterial = Material.getMaterial(obj.get("ReferenceBlock").getAsString());
        String recipeName = obj.get("Recipe").getAsString();
        Material openGUIBlockType = Material.getMaterial(obj.get("OpenGUIBlockType").getAsString());
        Recipe recipe =
                CustomRecipesAPI.getImplementation().getRecipesManager().getRecipes().stream().filter(r -> r.getRecipeKey().equalsIgnoreCase(recipeName)).findAny().orElse(null);
        List<PlayerFuel> fuel = jsonDeserializationContext.deserialize(obj.get("Fuel"), List.class);
        Location referenceBlockLocation = jsonDeserializationContext.deserialize(obj.get("ReferenceBlockLocation"), Location.class);
        Location openGUIBlockLocation = jsonDeserializationContext.deserialize(obj.get("OpenGUIBlockLocation"), Location.class);
        int machineFuelDeficiency = obj.get("FuelDeficiency").getAsInt();
        List<String> fuelTypes = jsonDeserializationContext.deserialize(obj.get("FuelTypes"), List.class);
        UUID owner = new UUID(obj.get("OwnerMost").getAsLong(), obj.get("OwnerLeast").getAsLong());
        int machineReach = obj.get("Reach").getAsInt();
        int machineMaxFuel = obj.get("MaxFuel").getAsInt();
        List<AbstractUpgrade> upgrades = jsonDeserializationContext.deserialize(obj.get("upgrades"), List.class);
        return factory.createMachine(referenceBlockMaterial, machineReach, machineMaxFuel,
                machineFuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType, referenceBlockLocation,
                machineTotalResourcesGained,
                machineResourcesGained, fuel, openGUIBlockLocation, machineZenCoinsGained, machineTotalZenCoinsGained, owner, upgrades);
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

        obj.add("Name", new JsonPrimitive(machine.getMachineName()));
        obj.add("Type", new JsonPrimitive(machine.getType().toString()));
        obj.add("structure", new JsonPrimitive(schematic));
        obj.add("ReferenceBlock", new JsonPrimitive(machine.getReferenceBlockType().toString()));
        obj.add("Recipe", new JsonPrimitive(machine.getRecipe().getRecipeKey()));
        obj.add("FuelDeficiency", new JsonPrimitive(machine.getFuelDeficiency()));
        obj.add("FuelTypes", jsonSerializationContext.serialize(machine.getFuelTypes()));
        obj.add("Reach", new JsonPrimitive(machine.getMachineReach()));
        obj.add("MaxFuel", new JsonPrimitive(machine.getMaxFuel()));
        obj.add("OpenGUIBlockType", new JsonPrimitive(machine.getOpenGUIBlockType().toString()));
        obj.add("TotalResourcesGained", new JsonPrimitive(machine.getTotalResourcesGained()));
        obj.add("ResourcesGained", jsonSerializationContext.serialize(machine.getResourcesGained(), List.class));
        obj.add("ZenCoinsGained", new JsonPrimitive(machine.getZenCoinsGained()));
        obj.add("TotalZenCoinsGained", new JsonPrimitive(machine.getTotalZenCoinsGained()));
        obj.add("ReferenceBlockLocation", jsonSerializationContext.serialize(machine.getReferenceBlockLocation(), Location.class));
        obj.add("OpenGUIBlockLocation", jsonSerializationContext.serialize(machine.getOpenGUIBlockLocation(), Location.class));
        obj.add("OwnerLeast", new JsonPrimitive(machine.getOwner().getLeastSignificantBits()));
        obj.add("OwnerMost", new JsonPrimitive(machine.getOwner().getMostSignificantBits()));
        obj.add("Fuel", jsonSerializationContext.serialize(machine.getFuels(), List.class));
        obj.add("upgrades", jsonSerializationContext.serialize(machine.getUpgrades(), List.class));
        return obj;
    }
}

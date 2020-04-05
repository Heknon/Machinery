package me.oriharel.machinery.serialization;

import com.google.gson.*;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.machine.Machine;
import me.oriharel.machinery.machine.MachineType;
import me.oriharel.machinery.machine.PlayerMachine;
import me.oriharel.machinery.machine.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerMachineTypeAdapter implements JsonSerializer<PlayerMachine>, JsonDeserializer<PlayerMachine> {
    @Override
    public PlayerMachine deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        String machineName = obj.get("machineName").getAsString();
        MachineType machineType = MachineType.valueOf(obj.get("machineType").getAsString());
        List<ItemStack> machineTotalResourcesGained = jsonDeserializationContext.deserialize(obj.get("machineTotalResourcesGained"), List.class);
        Structure structure = jsonDeserializationContext.deserialize(obj.get("structure"), Structure.class);
        int speed = obj.get("machineSpeed").getAsInt();
        Material referenceBlockMaterial = Material.getMaterial(obj.get("machineReferenceBlock").getAsString());
        String recipeName = obj.get("machineRecipe").getAsString();
        Recipe recipe =
                CustomRecipesAPI.getImplementation().getRecipesManager().getRecipes().stream().filter(r -> r.getRecipeKey().equalsIgnoreCase(recipeName)).findAny().orElse(null);
        List<Fuel> fuel = jsonDeserializationContext.deserialize(obj.get("machineFuel"), List.class);
        Location location = jsonDeserializationContext.deserialize(obj.get("machineLocation"), Location.class);
        int machineFuelDeficiency = obj.get("machineFuelDeficiency").getAsInt();
        List<String> fuelTypes = jsonDeserializationContext.deserialize(obj.get("machineFuelTypes"), List.class);
        int machineReach = obj.get("machineReach").getAsInt();
        int machineMaxFuel = obj.get("machineMaxFuel").getAsInt();
        return Machinery.getInstance().getMachineManager().getMachineFactory().createMachine(referenceBlockMaterial, machineReach, speed, machineMaxFuel,
                machineFuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, location, machineTotalResourcesGained, fuel);
    }

    @Override
    public JsonElement serialize(PlayerMachine machine, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.add("machineName", new JsonPrimitive(machine.getMachineName()));
        obj.add("machineType", new JsonPrimitive(machine.getType().toString()));
        obj.add("machineTotalResourcesGained", jsonSerializationContext.serialize(machine.getTotalResourcesGained()));
        obj.add("structure", jsonSerializationContext.serialize(machine.getStructure()));
        obj.add("machineSpeed", new JsonPrimitive(machine.getSpeed()));
        obj.add("machineReferenceBlock", new JsonPrimitive(machine.getReferenceBlockType().toString()));
        obj.add("machineRecipe", new JsonPrimitive(machine.getRecipe().getRecipeKey()));
        obj.add("machineFuel", jsonSerializationContext.serialize(machine.getFuel()));
        obj.add("machineFuelDeficiency", new JsonPrimitive(machine.getFuelDeficiency()));
        obj.add("machineFuelTypes", jsonSerializationContext.serialize(machine.getFuelTypes()));
        obj.add("machineReach", new JsonPrimitive(machine.getMachineReach()));
        obj.add("machineLocation", jsonSerializationContext.serialize(machine.getLocation()));
        obj.add("machineMaxFuel", new JsonPrimitive(machine.getMaxFuel()));
        return obj;
    }
}
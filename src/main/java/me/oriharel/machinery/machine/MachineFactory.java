package me.oriharel.machinery.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.customrecipes.recipe.item.RecipeResultReference;
import me.oriharel.customrecipes.serialize.NBTTagCompound;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.exceptions.MaterialNotFoundException;
import me.oriharel.machinery.exceptions.NotMachineTypeException;
import me.oriharel.machinery.exceptions.RecipeNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.serialization.PlayerMachineTypeAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class MachineFactory {

    private Machinery machinery;

    public MachineFactory(Machinery machinery) {
        this.machinery = machinery;
    }

    @Nullable
    public Machine createMachine(String machineKey) throws IllegalArgumentException, MachineNotFoundException, MaterialNotFoundException,
            RecipeNotFoundException, NotMachineTypeException {
        if (machineKey == null) throw new IllegalArgumentException("Machine machineKey must not be null (MachineFactory)");
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        if (!configLoad.isConfigurationSection(machineKey)) throw new MachineNotFoundException("The machine with the key of \"" + machineKey + "\" was not found in " +
                "machines.yml.");
        ConfigurationSection section = machinery.getFileManager().getConfig("machines.yml").get().getConfigurationSection(machineKey);
        assert section != null;
        String referenceBlockTypeString = section.getString("reference_block_type");
        if (referenceBlockTypeString == null) throw new MaterialNotFoundException("You must give a reference_block_type in the machine section of machine " + machineKey);
        Material referenceBlockType = Material.getMaterial(referenceBlockTypeString);
        if (referenceBlockType == null) throw new MaterialNotFoundException("No material named \"" + referenceBlockTypeString + "\" was found.");
        int machineReach = section.getInt("mine_radius", 0);
        int speed = section.getInt("speed", 0);
        int maxFuel = section.getInt("max_fuel", 0);
        int fuelDeficiency = section.getInt("deficiency", 0);
        List<String> fuelTypes = section.getStringList("fuel_types");
        String recipeName = section.getString("recipe", "");
        if (recipeName == null) throw new NullPointerException("No recipe given in machine section for machine named " + machineKey);
        Recipe recipe = CustomRecipesAPI.getImplementation().getRecipesManager().getRecipeByName(recipeName);
        if (recipe == null) throw new RecipeNotFoundException("Recipe with the name " + recipeName + " given in the machine section of " + machineKey + " has not been " +
                "located.");
        MachineType machineType = MachineType.getMachine(section.getString("type", null));
        Structure structure = new Structure(new File(machinery.getDataFolder(), "structures/" + machineKey + ".schem"), machineKey);
        Machine machine = new Machine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, machineType, structure, recipe, machineKey);
        injectMachineNBTIntoRecipe(recipe, machine);
        return machine;
    }

    @Nullable
    public Machine createMachine(Material referenceBlockType,
                                 int machineReach,
                                 int speed,
                                 int maxFuel,
                                 int fuelDeficiency,
                                 List<String> fuelTypes,
                                 MachineType machineType,
                                 Structure structure,
                                 Recipe recipe,
                                 String machineKey) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        Machine machine = new Machine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, machineType, structure,
                recipe, machineKey);
        injectMachineNBTIntoRecipe(machine.getRecipe(), machine);
        return machine;
    }

    @Nullable
    public PlayerMachine createMachine(Material referenceBlockType,
                                 int machineReach,
                                 int speed,
                                 int maxFuel,
                                 int fuelDeficiency,
                                 List<String> fuelTypes,
                                 MachineType machineType,
                                 Structure structure,
                                 Recipe recipe,
                                 String machineKey, Location location, List<ItemStack> totalResourcesGained, List<Fuel> fuels) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        return new PlayerMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, machineType, structure,
                recipe, machineKey, location, totalResourcesGained, fuels);
    }

    @Nullable
    public PlayerMachine createMachine(Machine machine, Location location, List<ItemStack> totalResourcesGained, List<Fuel> fuels) throws IllegalArgumentException {
        if (machine == null) throw new IllegalArgumentException("Machine must not be null (MachineFactory)");
        return new PlayerMachine(machine.referenceBlockType, machine.machineReach, machine.speed, machine.maxFuel, machine.fuelDeficiency, machine.fuelTypes, machine.machineType, machine.structure,
                machine.recipe, machine.machineName, location, totalResourcesGained, fuels);
    }

    /**
     * create PlayerMachine object from machine registry
     *
     * @param machineLocation location of the machine reference block
     * @return PlayerMachine or null if not found
     */
    public PlayerMachine createMachine(Location machineLocation) {
        String configKey =
                machineLocation.getBlockX() + "|" + machineLocation.getBlockY() + "|" + machineLocation.getBlockZ() + "|" + machineLocation.getWorld().getUID().toString();
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machine_registry.yml").get();
        String machineJson = configLoad.getString(configKey);
        Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(PlayerMachine.class, new PlayerMachineTypeAdapter()).setPrettyPrinting().create();
        return gson.fromJson(machineJson, PlayerMachine.class);
    }

    private void injectMachineNBTIntoRecipe(Recipe recipe, Machine machine) {
        RecipeResultReference recipeResultReference = recipe.getResult();
        Field nbtTagField;
        try {
            nbtTagField = RecipeResultReference.class.getSuperclass().getDeclaredField("nbtTagCompound");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }
        nbtTagField.setAccessible(true);
        NBTTagCompound nbtTagCompound = recipeResultReference.getNBTTagCompound();
        if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Machine.class, new MachineTypeAdapter()).create();
        nbtTagCompound.setString("machine", gson.toJson(machine));
        try {
            nbtTagField.set(recipeResultReference, nbtTagCompound);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        recipe.setRecipe(recipe.constructRecipe());
        CustomRecipesAPI.getImplementation().getRecipesManager().replaceRecipeNamed(recipe.getRecipeKey(), recipe);
    }
}

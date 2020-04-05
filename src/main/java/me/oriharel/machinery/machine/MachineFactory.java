package me.oriharel.machinery.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.customrecipes.recipe.item.RecipeResultReference;
import me.oriharel.customrecipes.serialize.NBTTagCompound;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class MachineFactory {

    private Machinery machinery;

    public MachineFactory(Machinery machinery) {
        this.machinery = machinery;
    }

    @Nullable
    public Machine createMachine(String machineKey, MachineType machineType) throws IllegalArgumentException, MachineNotFoundException {
        if (machineType == null || machineKey == null) throw new IllegalArgumentException("Machine type or machineKey must not be null (MachineFactory)");
        YamlConfiguration configLoad = machinery.getFileManager().getConfig("machines.yml").get();
        if (!configLoad.isConfigurationSection(machineKey)) throw new MachineNotFoundException("The machine with the key of \"" + machineKey + "\" was not found in " +
                "machines.yml.");
        ConfigurationSection section = machinery.getFileManager().getConfig("machines.yml").get().getConfigurationSection(machineKey);
        String referenceBlockTypeString = section.getString("")
        Material referenceBlockType = Material.getMaterial()
        new Machine()
        Machine machine = null;
        try {
            switch (machineType) {
                case LUMBERJACK:
                    machine = new LumberjackMachine(machineKey);
                    break;
                case EXCAVATOR:
                    machine = new ExcavatorMachine(machineKey);
                    break;
                case MINER:
                    machine = new MineMachine(machineKey);
                    break;
                case FARMER:
                    machine = new FarmMachine(machineKey);
                    break;
            }
        } catch (IOException | MachineNotFoundException e) {
            e.printStackTrace();
        }
        return machine;
    }

    @Nullable
    public Machine createMachine(Material referenceBlockType,
                                 int machineReach,
                                 int speed,
                                 int maxFuel,
                                 int fuelDeficiency,
                                 List<String> fuelTypes,
                                 List<Fuel> fuel,
                                 MachineType machineType,
                                 Structure structure,
                                 Recipe recipe,
                                 String machineKey,
                                 List<ItemStack> totalResourcesGained, MachineBlock machineBlock) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        Machine machine = null;
        switch (machineType) {
            case LUMBERJACK:
                machine = new LumberjackMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, fuel, machineType, structure,
                        recipe, machineKey, totalResourcesGained, machineBlock);
                break;
            case EXCAVATOR:
                machine = new ExcavatorMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, fuel, machineType, structure,
                        recipe, machineKey, totalResourcesGained, machineBlock);
                break;
            case MINER:
                machine = new MineMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, fuel, machineType, structure,
                        recipe, machineKey, totalResourcesGained, machineBlock);
                break;
            case FARMER:
                machine = new FarmMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, fuel, machineType, structure,
                        recipe, machineKey, totalResourcesGained, machineBlock);
                break;
        }
        injectMachineNBTIntoRecipe(machine.getRecipe(), machine);
        return machine;
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

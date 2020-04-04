package me.oriharel.machinery.machine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.oriharel.customrecipes.api.CustomRecipesAPI;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.customrecipes.recipe.item.RecipeResultReference;
import me.oriharel.customrecipes.serialize.NBTTagCompound;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class Machine implements IMachine {

    protected Material referenceBlockType;
    protected List<Fuel> fuelTypes;
    protected double cost;
    protected MachineType machineType;
    protected Structure structure;
    protected Recipe recipe;
    protected String machineName;
    protected int machineReach;
    protected int fuelDeficiency;
    protected List<Fuel> fuel;
    protected int fuelPerUse;
    protected int speed;
    protected int maxFuel;
    protected MachineBlock machineBlock;
    protected List<ItemStack> totalResourcesGained;
    transient private ConfigurationSection machineSection;

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<Fuel> fuelTypes,
            double cost,
            List<Fuel> fuel,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName,
            List<ItemStack> totalResourcesGained, MachineBlock machineBlock) {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.cost = cost;
        this.fuel = fuel;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.totalResourcesGained = totalResourcesGained;
        this.machineBlock = machineBlock;
    }

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<Fuel> fuelTypes,
            double cost,
            List<Fuel> fuel,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName) {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.cost = cost;
        this.fuel = fuel;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.totalResourcesGained = new ArrayList<ItemStack>();
    }

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<Fuel> fuelTypes,
            double cost,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName) {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.cost = cost;
        this.recipe = recipe;
        this.machineName = machineName;
        this.fuel = new ArrayList<>();
        this.machineType = machineType;
        this.structure = structure;
        this.totalResourcesGained = new ArrayList<ItemStack>();
    }

    public Machine(String machineName) throws MachineNotFoundException, IOException {
        this.machineName = machineName;
        YamlConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig("machines.yml")
                        .get();
        this.machineSection = configLoad.getConfigurationSection(machineName);
        if (this.machineSection == null)
            throw new MachineNotFoundException(
                    "The machine named \""
                            + machineName
                            + "\" does not have a section named \""
                            + machineName
                            + "\"in the machines.yml");
        getFuel();
        getFuelDeficiency();
        getFuelTypes();
        getMachineBlock();
        getMachineReach();
        getMaxFuel();
        getRecipe();
        getReferenceBlockType();
        getSpeed();
        getStructure();
        getTotalResourcesGained();
        getType();
        getCost();
    }

    @Override
    public Material getReferenceBlockType() {
        if (machineSection != null && referenceBlockType == null) this.referenceBlockType = Material.getMaterial(this.machineSection.getString("reference_block_type",
                "___"));
        return referenceBlockType;
    }

    @Override
    public int getMachineReach() {
        if (machineSection != null && machineReach == -1) this.machineReach = this.machineSection.getInt("mine_radius", 0);
        return machineReach;
    }

    @Override
    public int getSpeed() {
        if (machineSection != null && speed == -1) this.speed = this.machineSection.getInt("speed", 0);
        return speed;
    }

    @Override
    public int getMaxFuel() {
        if (machineSection != null && maxFuel == -1) this.maxFuel = this.machineSection.getInt("max_fuel", 0);
        return maxFuel;
    }

    @Override
    public int getFuelDeficiency() {
        if (machineSection != null && fuelDeficiency == -1) this.fuelDeficiency = this.machineSection.getInt("deficiency", 0);
        return fuelDeficiency;
    }

    @Override
    public List<Fuel> getFuelTypes() {
        if (machineSection != null && fuelTypes == null) this.fuelTypes = new ArrayList<>();
        return fuelTypes;
    }

    @Override
    public MachineBlock getMachineBlock() {
        if (machineSection != null && machineBlock == null) this.machineBlock = new MachineBlock(getRecipe(), this);
        return machineBlock;
    }

    public void setMachineBlock(MachineBlock machineBlock) {
        this.machineBlock = machineBlock;
    }

    public Recipe getRecipe() {
        if (machineSection != null && recipe == null) {
            String recipe = this.machineSection.getString("recipe");
            AtomicReference<String> currRecipeName = new AtomicReference<>("");
            try {
                Bukkit.getLogger().log(Level.INFO, "[Machinery] Loading machine recipe: " + recipe);
                this.recipe =
                        CustomRecipesAPI.getImplementation().getRecipesManager().getRecipes().stream().filter(rec -> {
                            currRecipeName.set(rec.getRecipeKey());
                            return rec.getRecipeKey().equalsIgnoreCase(recipe);
                        }).findFirst().get();
                try {
                    RecipeResultReference recipeResultReference = this.recipe.getResult();
                    Field nbtTagField = RecipeResultReference.class.getSuperclass().getDeclaredField("nbtTagCompound");
                    nbtTagField.setAccessible(true);
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) recipeResultReference.getNBTTagCompound();
                    if (nbtTagCompound == null) nbtTagCompound = new NBTTagCompound();
                    Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Machine.class, new MachineTypeAdapter()).create();
                    nbtTagCompound.setString("machine", gson.toJson(this));
                    nbtTagField.set(recipeResultReference, nbtTagCompound);
                    this.recipe.setRecipe(this.recipe.constructRecipe());
                    CustomRecipesAPI.getImplementation().getRecipesManager().replaceRecipeNamed(recipe, this.recipe);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                RecipeResultReference recipeResultReference = this.recipe.getResult();
                Bukkit.getLogger().log(Level.INFO, "[Machinery] Loaded machine recipe: " + recipe);
            } catch (NoSuchElementException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to find CustomRecipe with key of: " + currRecipeName.get());
            }
        }
        return this.recipe;
    }

    @Override
    public List<Fuel> getFuel() {
        if (fuel == null) this.fuel = new ArrayList<>();
        return fuel;
    }

    @Override
    public MachineType getType() {
        if (machineType == null && this instanceof ExcavatorMachine) this.machineType = MachineType.EXCAVATOR;
        else if (machineType == null && this instanceof LumberjackMachine) this.machineType = MachineType.LUMBERJACK;
        else if (machineType == null && this instanceof FarmMachine) this.machineType = MachineType.FARMER;
        else if (machineType == null && this instanceof MineMachine) this.machineType = MachineType.MINER;
        else if (machineSection != null && machineType == null) this.machineType = MachineType.valueOf(this.machineSection.getString("type"));
        return machineType;
    }

    @Override
    public List<ItemStack> getTotalResourcesGained() {
        if (totalResourcesGained == null) this.totalResourcesGained = new ArrayList<>();
        return totalResourcesGained;
    }

    @Override
    public List<ItemStack> run() {
        return null;
    }

    @Override
    public boolean build(UUID playerUuid, Location loc) {
        PreMachineBuildEvent preMachineBuildEvent = new PreMachineBuildEvent(this, loc);
        Bukkit.getPluginManager().callEvent(preMachineBuildEvent);
        if (preMachineBuildEvent.isCancelled()) return false;
        structure.build(loc, (success) -> {
            PostMachineBuildEvent postMachineBuildEvent = new PostMachineBuildEvent(this, loc);
            Bukkit.getPluginManager().callEvent(postMachineBuildEvent);
            PlayerMachine playerMachine = new PlayerMachine(this, loc);
            Machinery.getInstance().getMachineManager().registerPlayerMachine(playerUuid, playerMachine);
            return true;
        });
        return true;
    }

    @Override
    public double getCost() {
        if (cost == -1 && machineSection != null) cost = machineSection.getInt("cost");
        return cost;
    }

    @Override
    public Structure getStructure() {
        if (machineSection != null && structure == null)
            this.structure = new Structure(new File(Machinery.getInstance().getDataFolder(), "structures/" + machineName), machineName);
        return structure;
    }

    @Override
    public String getMachineName() {
        return machineName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return machineReach == machine.machineReach
                && speed == machine.speed
                && maxFuel == machine.maxFuel
                && fuelDeficiency == machine.fuelDeficiency
                && Double.compare(machine.cost, cost) == 0
                && fuelPerUse == machine.fuelPerUse
                && referenceBlockType == machine.referenceBlockType
                && Objects.equals(fuelTypes, machine.fuelTypes)
                && Objects.equals(fuel, machine.fuel)
                && machineType == machine.machineType
                && Objects.equals(structure, machine.structure)
                && Objects.equals(recipe, machine.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                referenceBlockType,
                machineReach,
                speed,
                maxFuel,
                fuelDeficiency,
                fuelTypes,
                cost,
                fuel,
                fuelPerUse,
                machineType,
                structure,
                recipe);
    }

    @Override
    public String toString() {
        return "Machine{"
                + "referenceBlockType="
                + referenceBlockType
                + ", machineReach="
                + machineReach
                + ", speed="
                + speed
                + ", maxFuel="
                + maxFuel
                + ", fuelDeficiency="
                + fuelDeficiency
                + ", fuelTypes="
                + fuelTypes
                + ", cost="
                + cost
                + ", fuel="
                + fuel
                + ", fuelPerUse="
                + fuelPerUse
                + ", machineType="
                + machineType
                + ", structure="
                + structure
                + ", recipe="
                + recipe
                + ", totalResourcesGained="
                + totalResourcesGained
                + '}';
    }
}

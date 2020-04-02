package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Machine implements IMachine, Serializable {

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
    private ConfigurationSection machineSection;

    private List<ItemStack> totalResourcesGained;

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<Fuel> fuelTypes,
            double cost,
            List<Fuel> fuel,
            int fuelPerUse,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName,
            List<ItemStack> totalResourcesGained)
            throws MachineNotFoundException {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.cost = cost;
        this.fuel = fuel;
        this.fuelPerUse = fuelPerUse;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.totalResourcesGained = totalResourcesGained;
        FileConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig(new File(Machinery.getInstance().getDataFolder(), "machines.yml"))
                        .getFileConfiguration();
        this.machineSection = configLoad.getConfigurationSection(machineName);
        if (this.machineSection == null)
            throw new MachineNotFoundException(
                    "The machine named \""
                            + machineName
                            + "\" does not have a section named \""
                            + machineName
                            + "\"in the machines.yml");
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
            int fuelPerUse,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName)
            throws MachineNotFoundException {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.cost = cost;
        this.fuel = fuel;
        this.fuelPerUse = fuelPerUse;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.totalResourcesGained = new ArrayList<ItemStack>();
        FileConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig(new File(Machinery.getInstance().getDataFolder(), "machines.yml"))
                        .getFileConfiguration();
        this.machineSection = configLoad.getConfigurationSection(machineName);
        if (this.machineSection == null)
            throw new MachineNotFoundException(
                    "The machine named \""
                            + machineName
                            + "\" does not have a section named \""
                            + machineName
                            + "\"in the machines.yml");
    }

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<Fuel> fuelTypes,
            double cost,
            int fuelPerUse,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName)
            throws MachineNotFoundException {
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
        this.fuelPerUse = fuelPerUse;
        this.machineType = machineType;
        this.structure = structure;
        this.totalResourcesGained = new ArrayList<ItemStack>();
        FileConfiguration configLoad =
                Machinery.getInstance()
                        .getFileManager()
                        .getConfig(new File(Machinery.getInstance().getDataFolder(), "machines.yml"))
                        .getFileConfiguration();
        this.machineSection = configLoad.getConfigurationSection(machineName);
        if (this.machineSection == null)
            throw new MachineNotFoundException(
                    "The machine named \""
                            + machineName
                            + "\" does not have a section named \""
                            + machineName
                            + "\"in the machines.yml");
    }

    @Override
    public Material getReferenceBlockType() {
        if (referenceBlockType == null) this.referenceBlockType = Material.getMaterial(this.machineSection.getString("reference_block_type", "___"));
        return referenceBlockType;
    }

    @Override
    public int getMachineReach() {
        return machineReach;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getMaxFuel() {
        return maxFuel;
    }

    @Override
    public int getFuelDeficiency() {
        return fuelDeficiency;
    }

    @Override
    public List<Fuel> getFuelTypes() {
        return fuelTypes;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public MachineBlock getMachineBlock() {
        return null;
    }

    @Override
    public List<Fuel> getFuel() {
        return fuel;
    }

    @Override
    public int getFuelPerUse() {
        return fuelPerUse;
    }

    @Override
    public MachineType getType() {
        return machineType;
    }

    @Override
    public List<ItemStack> getTotalResourcesGained() {
        return totalResourcesGained;
    }

    @Override
    public List<ItemStack> run() {
        return null;
    }

    @Override
    public boolean build(Location loc) {
        PreMachineBuildEvent preMachineBuildEvent = new PreMachineBuildEvent(this);
        Bukkit.getPluginManager().callEvent(preMachineBuildEvent);
        if (preMachineBuildEvent.isCancelled()) return false;
        structure.build(loc, () -> {
            PostMachineBuildEvent postMachineBuildEvent = new PostMachineBuildEvent(this);
            Bukkit.getPluginManager().callEvent(postMachineBuildEvent);
        });
        return true;
    }

    @Override
    public Structure getStructure() {
        return structure;
    }

    @Override
    public Recipe getRecipe() {
        return recipe;
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

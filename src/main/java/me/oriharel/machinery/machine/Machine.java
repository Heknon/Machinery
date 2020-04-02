package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Machine implements IMachine {

    private final Material referenceBlockType;
    private final int machineReach;
    private final int speed;
    private final int maxFuel;
    private final int fuelDeficiency;
    private final List<Fuel> fuelTypes;
    private final double cost;
    private final List<Fuel> fuel;
    private final int fuelPerUse;
    private final MachineType machineType;
    private final Structure structure;
    private final Recipe recipe;
    private final String machineName;

    private List<MachineProduce> totalResourcesGained;

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
            Recipe recipe, String machineName, List<MachineProduce> totalResourcesGained) {
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
            Structure structure, Recipe recipe, String machineName) {
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
        this.totalResourcesGained = new ArrayList<>();
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
            Structure structure, Recipe recipe, String machineName) {
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
        this.totalResourcesGained = new ArrayList<>();
    }

    @Override
    public Material getReferenceBlockType() {
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
    public List<MachineProduce> getTotalResourcesGained() {
        return totalResourcesGained;
    }

    @Override
    public List<MachineProduce> run() {
        return null;
    }

    @Override
    public boolean build() {
        return false;
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
        return machineReach == machine.machineReach &&
                speed == machine.speed &&
                maxFuel == machine.maxFuel &&
                fuelDeficiency == machine.fuelDeficiency &&
                Double.compare(machine.cost, cost) == 0 &&
                fuelPerUse == machine.fuelPerUse &&
                referenceBlockType == machine.referenceBlockType &&
                Objects.equals(fuelTypes, machine.fuelTypes) &&
                Objects.equals(fuel, machine.fuel) &&
                machineType == machine.machineType &&
                Objects.equals(structure, machine.structure) &&
                Objects.equals(recipe, machine.recipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe);
    }

    @Override
    public String toString() {
        return "Machine{" +
                "referenceBlockType=" + referenceBlockType +
                ", machineReach=" + machineReach +
                ", speed=" + speed +
                ", maxFuel=" + maxFuel +
                ", fuelDeficiency=" + fuelDeficiency +
                ", fuelTypes=" + fuelTypes +
                ", cost=" + cost +
                ", fuel=" + fuel +
                ", fuelPerUse=" + fuelPerUse +
                ", machineType=" + machineType +
                ", structure=" + structure +
                ", recipe=" + recipe +
                ", totalResourcesGained=" + totalResourcesGained +
                '}';
    }
}

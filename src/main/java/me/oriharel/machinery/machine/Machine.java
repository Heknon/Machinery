package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Machine implements IMachine {

    final protected Material referenceBlockType;
    final protected List<String> fuelTypes;
    final protected MachineType machineType;
    final protected Structure structure;I gue
    final protected Recipe recipe;
    final protected String machineName;
    final protected int machineReach;
    final protected int fuelDeficiency;
    final protected List<Fuel> fuel;
    final protected int speed;
    final protected int maxFuel;
    final protected MachineBlock machineBlock;
    final protected List<ItemStack> totalResourcesGained;

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<String> fuelTypes,
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
        this.fuel = fuel;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.totalResourcesGained = totalResourcesGained;
        this.machineBlock = machineBlock;
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
    public List<String> getFuelTypes() {
        return fuelTypes;
    }

    @Override
    public MachineBlock getMachineBlock() {
        return machineBlock;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    public List<Fuel> getFuel() {
        return fuel;
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
    public Structure getStructure() {
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
        return Double.compare(machine.cost, cost) == 0 &&
                machineReach == machine.machineReach &&
                fuelDeficiency == machine.fuelDeficiency &&
                fuelPerUse == machine.fuelPerUse &&
                speed == machine.speed &&
                maxFuel == machine.maxFuel &&
                referenceBlockType == machine.referenceBlockType &&
                Objects.equals(fuelTypes, machine.fuelTypes) &&
                machineType == machine.machineType &&
                Objects.equals(structure, machine.structure) &&
                Objects.equals(recipe, machine.recipe) &&
                Objects.equals(machineName, machine.machineName) &&
                Objects.equals(fuel, machine.fuel) &&
                Objects.equals(machineBlock, machine.machineBlock) &&
                Objects.equals(totalResourcesGained, machine.totalResourcesGained) &&
                Objects.equals(machineSection, machine.machineSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceBlockType, fuelTypes, cost, machineType, structure, recipe, machineName, machineReach, fuelDeficiency, fuel, fuelPerUse, speed,
                maxFuel, machineBlock, totalResourcesGained, machineSection);
    }

    @Override
    public String toString() {
        return "Machine{" +
                "referenceBlockType=" + referenceBlockType +
                ", fuelTypes=" + fuelTypes +
                ", cost=" + cost +
                ", machineType=" + machineType +
                ", structure=" + structure +
                ", recipe=" + recipe +
                ", machineName='" + machineName + '\'' +
                ", machineReach=" + machineReach +
                ", fuelDeficiency=" + fuelDeficiency +
                ", fuel=" + fuel +
                ", fuelPerUse=" + fuelPerUse +
                ", speed=" + speed +
                ", maxFuel=" + maxFuel +
                ", machineBlock=" + machineBlock +
                ", totalResourcesGained=" + totalResourcesGained +
                ", machineSection=" + machineSection +
                '}';
    }
}

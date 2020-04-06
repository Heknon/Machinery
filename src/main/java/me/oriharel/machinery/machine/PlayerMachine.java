package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.structure.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerMachine extends Machine {
    private final Location referenceBlockLocation;
    private final Location openGUIBlockLocation;
    private double totalResourcesGained;
    private List<ItemStack> resourcesGained;
    private List<Fuel> fuels;
    private List<Location> blockLocations;
    private double totalZenCoinsGained;
    private double zenCoinsGained;


    public PlayerMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<String> fuelTypes, MachineType machineType,
                         Structure structure, Recipe recipe, String machineName, Material openGUIBlockType, Location referenceBlockLocation,
                         double totalResourcesGained, List<ItemStack> resourcesGained, List<Fuel> fuels, Location openGUIBlockLocation,
                         List<Location> blockLocations, double zenCoinsGained,
                         double totalZenCoinsGained) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType);
        this.referenceBlockLocation = referenceBlockLocation;
        this.totalResourcesGained = totalResourcesGained;
        this.resourcesGained = resourcesGained;
        this.fuels = fuels;
        this.blockLocations = blockLocations;
        this.openGUIBlockLocation = openGUIBlockLocation;
        this.zenCoinsGained = zenCoinsGained;
        this.totalZenCoinsGained = totalZenCoinsGained;
    }


    public MachineBlock deconstruct() {
        return null;
    }

    public List<ItemStack> run() {
        return null;
    }

    public List<Fuel> getFuels() {
        return fuels;
    }

    public double getTotalResourcesGained() {
        return totalResourcesGained;
    }

    public Location getReferenceBlockLocation() {
        return referenceBlockLocation;
    }

    public void setTotalResourcesGained(double totalResourcesGained) {
        this.totalResourcesGained = totalResourcesGained;
    }

    public void setResourcesGained(List<ItemStack> resourcesGained) {
        this.resourcesGained = resourcesGained;
    }

    public void setFuels(List<Fuel> fuels) {
        this.fuels = fuels;
    }

    public double getTotalZenCoinsGained() {
        return totalZenCoinsGained;
    }

    public void setTotalZenCoinsGained(double totalZenCoinsGained) {
        this.totalZenCoinsGained = totalZenCoinsGained;
    }

    public double getZenCoinsGained() {
        return zenCoinsGained;
    }

    public void setZenCoinsGained(double zenCoinsGained) {
        this.zenCoinsGained = zenCoinsGained;
    }

    @Override
    public String toString() {
        return "PlayerMachine{" +
                "referenceBlockLocation=" + referenceBlockLocation +
                ", openGUIBlockLocation=" + openGUIBlockLocation +
                ", totalResourcesGained=" + totalResourcesGained +
                ", resourcesGained=" + resourcesGained +
                ", fuels=" + fuels +
                ", referenceBlockType=" + referenceBlockType +
                ", fuelTypes=" + fuelTypes +
                ", machineType=" + machineType +
                ", structure=" + structure +
                ", machineName='" + machineName + '\'' +
                ", machineBlock=" + machineBlock +
                ", openGUIBlockType=" + openGUIBlockType +
                ", recipe=" + recipe +
                ", machineReach=" + machineReach +
                ", fuelDeficiency=" + fuelDeficiency +
                ", speed=" + speed +
                ", maxFuel=" + maxFuel +
                ", totalZenCoinsGained=" + totalZenCoinsGained +
                ", zenCoinsGained=" + zenCoinsGained +
                '}';
    }

    public Location getOpenGUIBlockLocation() {
        return openGUIBlockLocation;
    }

    public List<ItemStack> getResourcesGained() {
        return resourcesGained;
    }

    public List<Location> getBlockLocations() {
        return blockLocations;
    }
}

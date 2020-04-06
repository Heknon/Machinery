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
    private final Location location;
    private List<ItemStack> totalResourcesGained;
    private List<Fuel> fuels;


    public PlayerMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<String> fuelTypes, MachineType machineType,
                         Structure structure, Recipe recipe, String machineName, Location location, List<ItemStack> totalResourcesGained, List<Fuel> fuels) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, machineType, structure, recipe, machineName);
        this.location = location;
        this.totalResourcesGained = totalResourcesGained;
        this.fuels = fuels;
    }


    public MachineBlock deconstruct() {
        return null;
    }

    public List<ItemStack> run() {
        return null;
    }

    public List<Fuel> getFuel() {
        return fuels;
    }

    public List<ItemStack> getTotalResourcesGained() {
        return totalResourcesGained;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "PlayerMachine{" +
                "location=" + location +
                ", totalResourcesGained=" + totalResourcesGained +
                ", fuels=" + fuels +
                ", referenceBlockType=" + referenceBlockType +
                ", fuelTypes=" + fuelTypes +
                ", machineType=" + machineType +
                ", structure=" + structure +
                ", recipe=" + recipe +
                ", machineName='" + machineName + '\'' +
                ", machineBlock=" + machineBlock +
                ", machineReach=" + machineReach +
                ", fuelDeficiency=" + fuelDeficiency +
                ", speed=" + speed +
                ", maxFuel=" + maxFuel +
                '}';
    }
}

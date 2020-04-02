package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;
import org.bukkit.Material;

import java.util.List;

class LumberjackMachine extends Machine {

    public LumberjackMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                             List<Fuel> fuel, int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, List<MachineProduce> totalResourcesGained) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, totalResourcesGained);
    }

    public LumberjackMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                             List<Fuel> fuel, int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe);
    }

    public LumberjackMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                             int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuelPerUse, machineType, structure, recipe);
    }
}

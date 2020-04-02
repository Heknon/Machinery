package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

class FarmMachine extends Machine {
    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName, List<ItemStack> totalResourcesGained) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName,
                totalResourcesGained);
    }

    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName
        );
    }

    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, int fuelPerUse,
                       MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuelPerUse, machineType, structure, recipe, machineName);
    }

    @Override
    public List<ItemStack> run() {
        return super.run();
    }

    @Override
    public boolean build() {
        return super.build();
    }
}

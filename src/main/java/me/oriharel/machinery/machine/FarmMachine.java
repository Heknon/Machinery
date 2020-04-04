package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

class FarmMachine extends Machine {

    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       MachineType machineType, Structure structure, Recipe recipe, String machineName, List<ItemStack> totalResourcesGained, MachineBlock machineBlock) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure, recipe, machineName,
                totalResourcesGained, machineBlock);
    }

    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       MachineType machineType, Structure structure, Recipe recipe, String machineName) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure, recipe, machineName);
    }

    public FarmMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                       MachineType machineType, Structure structure, Recipe recipe, String machineName) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, machineType, structure, recipe, machineName);
    }

    public FarmMachine(String machineName) throws MachineNotFoundException, IOException {
        super(machineName);
    }

    @Override
    public List<ItemStack> run() {
        return super.run();
    }
}

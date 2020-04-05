package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

class LumberjackMachine extends Machine {

    public LumberjackMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<String> fuelTypes,
                             List<Fuel> fuel, MachineType machineType, Structure structure, Recipe recipe, String machineName, List<ItemStack> totalResourcesGained,
                             MachineBlock machineBlock) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, fuel, machineType, structure, recipe, machineName,
                totalResourcesGained, machineBlock);
    }

    @Override
    public List<ItemStack> run() {
        return super.run();
    }
}

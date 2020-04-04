package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

class ExcavatorMachine extends Machine {

    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            List<Fuel> fuel, int fuelPerUse, Structure structure, Recipe recipe, String machineName,
                            List<ItemStack> totalResourcesGained) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, MachineType.EXCAVATOR, structure, recipe, machineName,
                totalResourcesGained);
    }

    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            List<Fuel> fuel, int fuelPerUse, Structure structure, Recipe recipe, String machineName) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, MachineType.EXCAVATOR, structure, recipe, machineName);
    }

    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            int fuelPerUse, Structure structure, Recipe recipe, String machineName) {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuelPerUse, MachineType.EXCAVATOR, structure, recipe, machineName);
    }

    public ExcavatorMachine(String machineName) throws MachineNotFoundException, IOException {
        super(machineName);
    }

    @Override
    public List<ItemStack> run() {
        return super.run();
    }

}

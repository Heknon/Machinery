package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerMachine extends Machine {
    public PlayerMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                         int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName, List<ItemStack> totalResourcesGained) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName,
                totalResourcesGained);
    }

    public PlayerMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                         int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName);
    }

    public PlayerMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, int fuelPerUse,
                         MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuelPerUse, machineType, structure, recipe, machineName);
    }

    public PlayerMachine(Machine machine, Location loc) {
        super(machine.referenceBlockType, )
    }
}

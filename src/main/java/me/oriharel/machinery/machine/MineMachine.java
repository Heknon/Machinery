package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;
import org.bukkit.Material;

import java.util.List;

class MineMachine extends Machine {
    public MineMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName, List<MachineProduce> totalResourcesGained) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName,
                totalResourcesGained);
    }

    public MineMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, List<Fuel> fuel,
                       int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName
        );
    }

    public MineMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost, int fuelPerUse,
                       MachineType machineType, Structure structure, Recipe recipe, String machineName) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuelPerUse, machineType, structure, recipe, machineName);
    }

    @Override
    public List<MachineProduce> run() {
        return super.run();
    }

    @Override
    public boolean build() {
        return super.build();
    }
}

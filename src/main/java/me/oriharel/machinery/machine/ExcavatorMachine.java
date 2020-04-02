package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.items.MachineProduce;
import org.bukkit.Material;

import java.util.List;

class ExcavatorMachine extends Machine {
    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            List<Fuel> fuel, int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName, MachineBlock machineBlock
            , List<MachineProduce> totalResourcesGained) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName,
                totalResourcesGained);
    }

    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            List<Fuel> fuel, int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName,
                            MachineBlock machineBlock) throws MachineNotFoundException {
        super(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, fuelPerUse, machineType, structure, recipe, machineName
        );
    }

    public ExcavatorMachine(Material referenceBlockType, int machineReach, int speed, int maxFuel, int fuelDeficiency, List<Fuel> fuelTypes, double cost,
                            int fuelPerUse, MachineType machineType, Structure structure, Recipe recipe, String machineName, MachineBlock machineBlock) throws MachineNotFoundException {
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

package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.exceptions.MachineNotFoundException;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class MachineFactory {
    @Nullable
    public Machine createMachine(String machineName, MachineType machineType) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        Machine machine = null;
        try {
            switch (machineType) {
                case LUMBERJACK:
                    machine = new LumberjackMachine(machineName);
                    break;
                case EXCAVATOR:
                    machine = new ExcavatorMachine(machineName);
                    break;
                case MINER:
                    machine = new MineMachine(machineName);
                    break;
                case FARMER:
                    machine = new FarmMachine(machineName);
                    break;
            }
        } catch (IOException | MachineNotFoundException e) {
            e.printStackTrace();
        }
        return machine;
    }

    @Nullable
    public Machine createMachine(Material referenceBlockType,
                                 int machineReach,
                                 int speed,
                                 int maxFuel,
                                 int fuelDeficiency,
                                 List<Fuel> fuelTypes,
                                 double cost,
                                 List<Fuel> fuel,
                                 MachineType machineType,
                                 Structure structure,
                                 Recipe recipe,
                                 String machineName,
                                 List<ItemStack> totalResourcesGained, MachineBlock machineBlock) throws IllegalArgumentException {
        if (machineType == null) throw new IllegalArgumentException("Machine type must not be null (MachineFactory)");
        Machine machine = null;
        switch (machineType) {
            case LUMBERJACK:
                machine = new LumberjackMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure,
                        recipe, machineName, totalResourcesGained, machineBlock);
                break;
            case EXCAVATOR:
                machine = new ExcavatorMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure,
                        recipe, machineName, totalResourcesGained, machineBlock);
                break;
            case MINER:
                machine = new MineMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure,
                        recipe, machineName, totalResourcesGained, machineBlock);
                break;
            case FARMER:
                machine = new FarmMachine(referenceBlockType, machineReach, speed, maxFuel, fuelDeficiency, fuelTypes, cost, fuel, machineType, structure,
                        recipe, machineName, totalResourcesGained, machineBlock);
                break;
        }
        return machine;
    }
}

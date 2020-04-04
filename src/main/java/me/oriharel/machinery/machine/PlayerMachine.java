package me.oriharel.machinery.machine;

import me.oriharel.machinery.exceptions.MachineNotFoundException;
import org.bukkit.Location;

public class PlayerMachine extends Machine {
    private Location location;


    public PlayerMachine(Machine machine, Location loc) throws MachineNotFoundException {
        super(machine.referenceBlockType, machine.machineReach, machine.speed, machine.maxFuel, machine.fuelDeficiency, machine.fuelTypes, machine.cost, machine.fuel,
                machine.fuelPerUse, machine.machineType, machine.structure, machine.recipe, machine.machineName, machine.totalResourcesGained);
        this.location = loc;
    }

    public Location getLocation() {
        return location;
    }
}

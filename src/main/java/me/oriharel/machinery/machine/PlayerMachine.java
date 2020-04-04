package me.oriharel.machinery.machine;

import org.bukkit.Location;

public class PlayerMachine extends Machine {
    private Location location;


    public PlayerMachine(Machine machine, Location loc) {
        super(machine.referenceBlockType, machine.machineReach, machine.speed, machine.maxFuel, machine.fuelDeficiency, machine.fuelTypes, machine.cost, machine.fuel,
                machine.machineType, machine.structure, machine.recipe, machine.machineName, machine.totalResourcesGained, machine.machineBlock);
        this.location = loc;
    }

    public Location getLocation() {
        return location;
    }
}

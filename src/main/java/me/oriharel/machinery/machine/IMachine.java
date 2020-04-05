package me.oriharel.machinery.machine;

import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

interface IMachine {

    Material getReferenceBlockType();

    int getMachineReach();

    int getSpeed();

    int getMaxFuel();

    int getFuelDeficiency();

    List<String> getFuelTypes();

    MachineType getType();

    boolean build(UUID playerUuid, Location location);

    Structure getStructure();

    MachineBlock getMachineBlock();

    String getMachineName();
}

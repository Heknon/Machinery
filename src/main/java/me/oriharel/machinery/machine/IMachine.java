package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

interface IMachine {

    Material getReferenceBlockType();

    int getMachineReach();

    int getSpeed();

    int getMaxFuel();

    int getFuelDeficiency();

    List<Fuel> getFuelTypes();

    List<Fuel> getFuel();

    MachineType getType();

    List<ItemStack> getTotalResourcesGained();

    List<ItemStack> run();

    boolean build(Location location);

    Structure getStructure();

    MachineBlock getMachineBlock() throws IOException;

    String getMachineName();
}

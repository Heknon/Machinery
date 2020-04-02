package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.Fuel;
import me.oriharel.machinery.items.MachineProduce;
import org.bukkit.Material;

import java.util.List;

interface IMachine {

    Material getReferenceBlockType();

    int getMachineReach();

    int getSpeed();

    int getMaxFuel();

    int getFuelDeficiency();

    List<Fuel> getFuelTypes();

    double getCost();

    List<Fuel> getFuel();

    int getFuelPerUse();

    MachineType getType();

    List<MachineProduce> getTotalResourcesGained();

    List<MachineProduce> run();

    boolean build();

    Structure getStructure();

    Recipe getRecipe();

    String getMachineName();
}

package me.oriharel.machinery.machine;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.structure.Structure;
import org.bukkit.Material;

import java.util.List;

@JsonAdapter(MachineTypeAdapter.class)
public class Machine {

    final protected List<String> fuelTypes;
    final protected MachineType machineType;
    final protected Structure structure;
    final protected String machineName;
    final protected MachineBlock machineBlock;
    final protected Material machineCoreBlockType;
    protected Recipe recipe;
    protected int machineReach;
    protected int fuelDeficiency;
    protected int maxFuel;

    public Machine(
            int machineReach,
            int maxFuel,
            int fuelDeficiency,
            List<String> fuelTypes,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName, Material machineCoreBlockType) {
        this.machineReach = machineReach;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.machineCoreBlockType = machineCoreBlockType;
        this.machineBlock = new MachineBlock(recipe, this);
    }

    public Material getMachineCoreBlockType() {
        return machineCoreBlockType;
    }

    public int getMachineReach() {
        return machineReach;
    }

    public void setMachineReach(int machineReach) {
        this.machineReach = machineReach;
    }


    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }


    public int getFuelDeficiency() {
        return fuelDeficiency;
    }

    public void setFuelDeficiency(int fuelDeficiency) {
        this.fuelDeficiency = fuelDeficiency;
    }


    public List<String> getFuelTypes() {
        return fuelTypes;
    }


    public MachineBlock getMachineBlock() {
        return machineBlock;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }


    public MachineType getType() {
        return machineType;
    }


    public Structure getStructure() {
        return structure;
    }


    public String getMachineName() {
        return machineName;
    }

}

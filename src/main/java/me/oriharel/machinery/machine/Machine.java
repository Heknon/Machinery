package me.oriharel.machinery.machine;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.structure.Structure;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
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
    protected CustomRecipe<?> recipe;
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
            CustomRecipe<?> recipe,
            String machineName, Material machineCoreBlockType, MachineFactory factory) {
        this.machineReach = machineReach;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.machineCoreBlockType = machineCoreBlockType;
        this.machineBlock = new MachineBlock(recipe, this, factory);
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

    public CustomRecipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(CustomRecipe<?> recipe) {
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

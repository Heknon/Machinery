package me.oriharel.machinery.machine;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.machinery.items.MachineItem;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.structure.Structure;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.Material;

@JsonAdapter(MachineTypeAdapter.class)
public class Machine {

    final protected MachineType machineType;
    final protected Structure structure;
    final protected String machineName;
    final protected MachineItem machineItem;
    final protected Material machineCoreBlockType;
    protected CustomRecipe<?> recipe;
    protected int fuelDeficiency;
    protected int maxFuel;

    public Machine(
            int maxFuel,
            int fuelDeficiency,
            MachineType machineType,
            Structure structure,
            CustomRecipe<?> recipe,
            String machineName, Material machineCoreBlockType, MachineFactory factory) {
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.machineCoreBlockType = machineCoreBlockType;
        this.machineItem = new MachineItem(recipe, this, factory);
    }

    public Material getMachineCoreBlockType() {
        return machineCoreBlockType;
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


    public MachineItem getMachineItem() {
        return machineItem;
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

    @Override
    public String toString() {
        return "Machine{" +
                "machineType=" + machineType +
                ", structure=" + structure +
                ", machineName='" + machineName + '\'' +
                ", machineBlock=" + machineItem +
                ", machineCoreBlockType=" + machineCoreBlockType +
                ", recipe=" + recipe +
                ", fuelDeficiency=" + fuelDeficiency +
                ", maxFuel=" + maxFuel +
                '}';
    }
}

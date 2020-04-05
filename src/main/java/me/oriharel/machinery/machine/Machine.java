package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.items.MachineBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Machine implements IMachine {

    final protected Material referenceBlockType;
    final protected List<String> fuelTypes;
    final protected MachineType machineType;
    final protected Structure structure;
    final protected Recipe recipe;
    final protected String machineName;
    final protected MachineBlock machineBlock;
    protected int machineReach;
    protected int fuelDeficiency;
    protected int speed;
    protected int maxFuel;

    public Machine(
            Material referenceBlockType,
            int machineReach,
            int speed,
            int maxFuel,
            int fuelDeficiency,
            List<String> fuelTypes,
            MachineType machineType,
            Structure structure,
            Recipe recipe,
            String machineName) {
        this.referenceBlockType = referenceBlockType;
        this.machineReach = machineReach;
        this.speed = speed;
        this.maxFuel = maxFuel;
        this.fuelDeficiency = fuelDeficiency;
        this.fuelTypes = fuelTypes;
        this.machineType = machineType;
        this.structure = structure;
        this.recipe = recipe;
        this.machineName = machineName;
        this.machineBlock = new MachineBlock(recipe, this);
    }

    @Override
    public Material getReferenceBlockType() {
        return referenceBlockType;
    }

    @Override
    public int getMachineReach() {
        return machineReach;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getMaxFuel() {
        return maxFuel;
    }

    @Override
    public int getFuelDeficiency() {
        return fuelDeficiency;
    }

    @Override
    public List<String> getFuelTypes() {
        return fuelTypes;
    }

    @Override
    public MachineBlock getMachineBlock() {
        return machineBlock;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    public MachineType getType() {
        return machineType;
    }

    @Override
    public Structure getStructure() {
        return structure;
    }

    @Override
    public String getMachineName() {
        return machineName;
    }

    public void setMachineReach(int machineReach) {
        this.machineReach = machineReach;
    }

    public void setFuelDeficiency(int fuelDeficiency) {
        this.fuelDeficiency = fuelDeficiency;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    @Override
    public boolean build(UUID playerUuid, Location loc) {
        PreMachineBuildEvent preMachineBuildEvent = new PreMachineBuildEvent(this, loc);
        Bukkit.getPluginManager().callEvent(preMachineBuildEvent);
        if (preMachineBuildEvent.isCancelled()) return false;
        structure.build(loc, (success) -> {
            PostMachineBuildEvent postMachineBuildEvent = new PostMachineBuildEvent(this, loc);
            Bukkit.getPluginManager().callEvent(postMachineBuildEvent);
            PlayerMachine playerMachine = Machinery.getInstance().getMachineManager().getMachineFactory().createMachine(this, loc, new ArrayList<>(), new ArrayList<>());
            Machinery.getInstance().getMachineManager().registerPlayerMachine(playerUuid, playerMachine);
            return true;
        });
        return true;
    }
}

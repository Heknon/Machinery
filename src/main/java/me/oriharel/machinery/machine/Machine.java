package me.oriharel.machinery.machine;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.api.events.PostMachineBuildEvent;
import me.oriharel.machinery.api.events.PreMachineBuildEvent;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.serialization.MachineTypeAdapter;
import me.oriharel.machinery.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@JsonAdapter(MachineTypeAdapter.class)
public class Machine implements IMachine {

    final protected Material referenceBlockType;
    final protected List<String> fuelTypes;
    final protected MachineType machineType;
    final protected Structure structure;
    final protected String machineName;
    final protected MachineBlock machineBlock;
    final protected Material openGUIBlockType;
    protected Recipe recipe;
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
            String machineName, Material openGUIBlockType) {
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
        this.openGUIBlockType = openGUIBlockType;
        this.machineBlock = new MachineBlock(recipe, this);
    }

    @Override
    public Material getReferenceBlockType() {
        return referenceBlockType;
    }

    @Override
    public Material getOpenGUIBlockType() {
        return openGUIBlockType;
    }

    @Override
    public int getMachineReach() {
        return machineReach;
    }

    public void setMachineReach(int machineReach) {
        this.machineReach = machineReach;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    @Override
    public int getFuelDeficiency() {
        return fuelDeficiency;
    }

    public void setFuelDeficiency(int fuelDeficiency) {
        this.fuelDeficiency = fuelDeficiency;
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

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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

    @Override
    public boolean build(UUID playerUuid, Location loc) {
        PreMachineBuildEvent preMachineBuildEvent = new PreMachineBuildEvent(this, loc);
        Bukkit.getPluginManager().callEvent(preMachineBuildEvent);
        if (preMachineBuildEvent.isCancelled()) return false;
        Player p = Bukkit.getPlayer(playerUuid);
        List<Location> locations = structure.build(loc, p, this.referenceBlockType, this.openGUIBlockType, (printResult) -> {
            if (printResult.getPlacementLocations() == null) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Machinery.getInstance().getFileManager().getConfig("config.yml").get().getString(
                        "not_empty_place")));
                return false;
            }
            PlayerMachine playerMachine = Machinery.getInstance().getMachineManager().getMachineFactory().createMachine(this, printResult.getSpecialBlockLocation(),
                    printResult.getOpenGUIBlockLocation(), 0, new ArrayList<>(), new ArrayList<>(), 0, 0, playerUuid);
            Machinery.getInstance().getMachineManager().registerNewPlayerMachine(playerMachine, new HashSet<>(printResult.getPlacementLocations()));
            PostMachineBuildEvent postMachineBuildEvent = new PostMachineBuildEvent(playerMachine, loc);
            Bukkit.getPluginManager().callEvent(postMachineBuildEvent);
            return true;
        });
        if (locations == null) return false;
        Machinery.getInstance().getMachineManager().addTemporaryPreRegisterMachinePartLocations(locations);
        return true;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "referenceBlockType=" + referenceBlockType +
                ", fuelTypes=" + fuelTypes +
                ", machineType=" + machineType +
                ", structure=" + structure +
                ", recipe=" + recipe +
                ", machineName='" + machineName + '\'' +
                ", machineBlock=" + machineBlock +
                ", machineReach=" + machineReach +
                ", fuelDeficiency=" + fuelDeficiency +
                ", speed=" + speed +
                ", maxFuel=" + maxFuel +
                ", openGUIBlockType=" + openGUIBlockType +
                '}';
    }
}

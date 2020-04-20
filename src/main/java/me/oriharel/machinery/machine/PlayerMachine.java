package me.oriharel.machinery.machine;

import me.oriharel.customrecipes.recipe.Recipe;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.fuel.PlayerFuel;
import me.oriharel.machinery.items.MachineBlock;
import me.oriharel.machinery.structure.Structure;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerMachine extends Machine {
    private final Location machineCore;
    private final UUID owner;
    private final List<AbstractUpgrade> upgrades;
    private double totalResourcesGained;
    private HashMap<Material, ItemStack> resourcesGained;
    private List<PlayerFuel> fuels;
    private double totalZenCoinsGained;
    private double zenCoinsGained;
    private MachineResourceGetProcess machineResourceGetProcess;


    public PlayerMachine(int machineReach, int maxFuel, int fuelDeficiency, List<String> fuelTypes, MachineType machineType,
                         Structure structure, Recipe recipe, String machineName, Material openGUIBlockType,
                         double totalResourcesGained, HashMap<Material, ItemStack> resourcesGained, List<PlayerFuel> fuels, Location machineCore, double zenCoinsGained,
                         double totalZenCoinsGained, UUID owner, List<AbstractUpgrade> upgrades) {
        super(machineReach, maxFuel, fuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType);
        this.totalResourcesGained = totalResourcesGained;
        this.resourcesGained = resourcesGained;
        this.fuels = fuels;
        this.machineCore = machineCore;
        this.zenCoinsGained = zenCoinsGained;
        this.totalZenCoinsGained = totalZenCoinsGained;
        this.owner = owner;
        this.upgrades = upgrades;
    }


    public MachineBlock<PlayerMachine> deconstruct() {
        MachineManager machineManager = Machinery.getInstance().getMachineManager();
        machineManager.clearMachineTileStateDataFromBlock(machineCore.getBlock());
        Location[] machinePartLocations = machineManager.getPlayerMachineLocations(machineCore.getBlock());
        machineManager.unregisterPlayerMachine(this);
        machineResourceGetProcess.endProcess();


        for (Location loc : machinePartLocations) {
            Block block = loc.getBlock();
            block.setType(Material.AIR);
        }
        return new MachineBlock<>(this.recipe, this);
    }

    public MachineResourceGetProcess getMinerProcess() {
        if (machineResourceGetProcess == null)
            machineResourceGetProcess = new MachineResourceGetProcess(this);
        return machineResourceGetProcess;
    }

    public List<PlayerFuel> getFuels() {
        return fuels;
    }

    public void setFuels(List<PlayerFuel> fuels) {
        this.fuels = fuels;
    }

    public void addFuel(PlayerFuel fuel) {
        this.fuels.add(fuel);
    }

    public double getTotalResourcesGained() {
        return totalResourcesGained;
    }

    public void setTotalResourcesGained(double totalResourcesGained) {
        this.totalResourcesGained = totalResourcesGained;
    }

    public double getTotalZenCoinsGained() {
        return totalZenCoinsGained;
    }

    public void setTotalZenCoinsGained(double totalZenCoinsGained) {
        this.totalZenCoinsGained = totalZenCoinsGained;
    }

    public double getZenCoinsGained() {
        return zenCoinsGained;
    }

    public void setZenCoinsGained(double zenCoinsGained) {
        this.zenCoinsGained = zenCoinsGained;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getMachineCore() {
        return machineCore;
    }

    public HashMap<Material, ItemStack> getResourcesGained() {
        return resourcesGained;
    }

    public void setResourcesGained(HashMap<Material, ItemStack> resourcesGained) {
        this.resourcesGained = resourcesGained;
    }

    public List<AbstractUpgrade> getUpgrades() {
        return upgrades;
    }

}

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
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerMachine extends Machine {
    private final Location referenceBlockLocation;
    private final Location openGUIBlockLocation;
    private final UUID owner;
    private final List<AbstractUpgrade> upgrades;
    private double totalResourcesGained;
    private List<ItemStack> resourcesGained;
    private List<PlayerFuel> fuels;
    private double totalZenCoinsGained;
    private double zenCoinsGained;


    public PlayerMachine(Material referenceBlockType, int machineReach, int maxFuel, int fuelDeficiency, List<String> fuelTypes, MachineType machineType,
                         Structure structure, Recipe recipe, String machineName, Material openGUIBlockType, Location referenceBlockLocation,
                         double totalResourcesGained, List<ItemStack> resourcesGained, List<PlayerFuel> fuels, Location openGUIBlockLocation, double zenCoinsGained,
                         double totalZenCoinsGained, UUID owner, List<AbstractUpgrade> upgrades) {
        super(referenceBlockType, machineReach, maxFuel, fuelDeficiency, fuelTypes, machineType, structure, recipe, machineName, openGUIBlockType);
        this.referenceBlockLocation = referenceBlockLocation;
        this.totalResourcesGained = totalResourcesGained;
        this.resourcesGained = resourcesGained;
        this.fuels = fuels;
        this.openGUIBlockLocation = openGUIBlockLocation;
        this.zenCoinsGained = zenCoinsGained;
        this.totalZenCoinsGained = totalZenCoinsGained;
        this.owner = owner;
        this.upgrades = upgrades;
    }


    public MachineBlock deconstruct() {
        MachineManager machineManager = Machinery.getInstance().getMachineManager();
        Location[] machinePartLocations = machineManager.getPlayerMachineLocations(openGUIBlockLocation.getBlock());
        for (Location loc : machinePartLocations) {
            Block block = loc.getBlock();
            machineManager.clearMachineTileStateDataFromBlock(block);
            block.setType(Material.AIR);
        }
        Machinery.getInstance().getMachineManager().unregisterPlayerMachine(this);
        return new MachineBlock(this.recipe, this);
    }

    public MachineResourceGetProcess run() {
        return null;
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

    public Location getReferenceBlockLocation() {
        return referenceBlockLocation;
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

    public Location getOpenGUIBlockLocation() {
        return openGUIBlockLocation;
    }

    public List<ItemStack> getResourcesGained() {
        return resourcesGained;
    }

    public void setResourcesGained(List<ItemStack> resourcesGained) {
        this.resourcesGained = resourcesGained;
    }




    public List<AbstractUpgrade> getUpgrades() {
        return upgrades;
    }
}

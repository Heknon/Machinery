package me.oriharel.machinery.machine;

import com.google.gson.annotations.JsonAdapter;
import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.items.MachineItem;
import me.oriharel.machinery.serialization.AbstractUpgradeTypeAdapter;
import me.oriharel.machinery.structure.Structure;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerMachine extends Machine {
    private final Location machineCore;
    private final UUID owner;
    private final Set<UUID> playersWithAccessPermission;
    @JsonAdapter(AbstractUpgradeTypeAdapter.class)
    private final List<AbstractUpgrade> upgrades;
    private double totalResourcesGained;
    private HashMap<Material, ItemStack> resourcesGained;
    private int energyInMachine;
    private double totalZenCoinsGained;
    private double zenCoinsGained;
    private transient MachineResourceGetProcess machineResourceGetProcess;
    private transient MachineFactory factory;


    public PlayerMachine(int maxFuel, int fuelDeficiency, MachineType machineType,
                         Structure structure, CustomRecipe<?> recipe, String machineName, Material openGUIBlockType,
                         Set<UUID> playersWithAccessPermission, double totalResourcesGained, HashMap<Material, ItemStack> resourcesGained, int energyInMachine, Location machineCore, double zenCoinsGained,
                         double totalZenCoinsGained, UUID owner, List<AbstractUpgrade> upgrades, MachineFactory factory) {
        super(maxFuel, fuelDeficiency, machineType, structure, recipe, machineName, openGUIBlockType, factory);
        this.playersWithAccessPermission = playersWithAccessPermission;
        this.totalResourcesGained = totalResourcesGained;
        this.resourcesGained = resourcesGained;
        this.energyInMachine = energyInMachine;
        this.machineCore = machineCore;
        this.zenCoinsGained = zenCoinsGained;
        this.totalZenCoinsGained = totalZenCoinsGained;
        this.owner = owner;
        this.upgrades = upgrades;
        this.factory = factory;
    }


    /**
     * Clears all TileEntity block data from core block of machine
     * gets all locations of the blocks that make up the machine
     * unregisters the machine from the plugin
     * stops mining process
     * removes all machine related blocks
     *
     * @return MachineBlock that represents the deconstructed machine and the data in it
     */
    public MachineItem deconstruct() {
        MachineManager machineManager = Machinery.Companion.getInstance().getMachineManager();
        machineManager.clearMachineTileStateDataFromBlock(machineCore.getBlock());
        Location[] machinePartLocations = machineManager.getPlayerMachineLocations(machineCore.getBlock());
        machineManager.unregisterPlayerMachine(this);
        machineResourceGetProcess.endProcess();


        for (Location loc : machinePartLocations) {
            Block block = loc.getBlock();
            block.setType(Material.AIR);
        }
        return new MachineItem(this.recipe, this, factory);
    }

    public MachineResourceGetProcess getMinerProcess() {
        if (machineResourceGetProcess == null)
            machineResourceGetProcess = new MachineResourceGetProcess(this);
        return machineResourceGetProcess;
    }

    public int getEnergyInMachine() {
        return energyInMachine;
    }

    public void setEnergyInMachine(int energyInMachine) {
        this.energyInMachine = energyInMachine;
    }

    public void addEnergy(int energy) {
        this.energyInMachine += energy;
    }

    public void removeEnergy(int energy) {
        this.energyInMachine -= energy;
    }

    public double getTotalResourcesGained() {
        return totalResourcesGained;
    }

    public double getTotalZenCoinsGained() {
        return totalZenCoinsGained;
    }

    public double getZenCoinsGained() {
        return zenCoinsGained;
    }

    public void addZenCoinsGained(double zenCoinsGained) {
        this.totalZenCoinsGained += zenCoinsGained;
        this.zenCoinsGained += zenCoinsGained;
    }

    public void removeZenCoinsGained(double zenCoinsToRemove) {
        this.zenCoinsGained -= zenCoinsToRemove;
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

    public void setTotalResourcesGained(double totalResourcesGained) {
        this.totalResourcesGained = totalResourcesGained;
    }

    public void setTotalZenCoinsGained(double totalZenCoinsGained) {
        this.totalZenCoinsGained = totalZenCoinsGained;
    }

    public void setZenCoinsGained(double zenCoinsGained) {
        this.zenCoinsGained = zenCoinsGained;
    }

    public Set<UUID> getPlayersWithAccessPermission() {
        return playersWithAccessPermission;
    }

    @Override
    public String toString() {
        return "PlayerMachine{" +
                "machineCore=" + machineCore +
                ", owner=" + owner +
                ", playersWithAccessPermission=" + playersWithAccessPermission +
                ", upgrades=" + upgrades +
                ", totalResourcesGained=" + totalResourcesGained +
                ", resourcesGained=" + resourcesGained +
                ", energyInMachine=" + energyInMachine +
                ", totalZenCoinsGained=" + totalZenCoinsGained +
                ", zenCoinsGained=" + zenCoinsGained +
                ", machineResourceGetProcess=" + machineResourceGetProcess +
                '}';
    }

    public MachineFactory getFactory() {
        return factory;
    }
}

package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.MaterialChance;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.RandomCollection;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MachineResourceGetProcess {
    protected List<ItemStack> itemsGained;
    protected PlayerMachine machine;
    protected int minePeriod;
    protected BukkitRunnable process;
    protected RandomCollection<MaterialChance> materialChances;
    protected double lootAmplifier;
    protected Random random;

    public MachineResourceGetProcess(PlayerMachine machine) {
        itemsGained = new ArrayList<>();
        this.machine = machine;
        this.minePeriod = 20;
        this.random = new Random();
        this.materialChances = null;
        this.process = null;
        this.lootAmplifier = 1;
        initializeMaterialChances();
    }

    public void startProcess() {
        List<AbstractUpgrade> upgrades = machine.getUpgrades();
        upgrades.forEach(upgrade -> {
            upgrade.applyUpgradeModifier(this);
        });
        process = new BukkitRunnable() {
            @Override
            public void run() {
                getResources();
                upgrades.forEach(upgrade -> {
                    if (!upgrade.isRunOnlyOnProcessStart()) upgrade.applyUpgradeModifier(MachineResourceGetProcess.this);
                });
                insertResources();
            }
        };
        process.runTaskTimerAsynchronously(Machinery.getInstance(), 0, minePeriod);
    }

    public void endProcess() {
        process.cancel();
    }

    public boolean isRunning() {
        return !process.isCancelled();
    }

    protected void getResources() {
        List<ItemStack> itemsGained = this.itemsGained;


        if (materialChances == null) {
            initializeMaterialChances();
        }
        MaterialChance chance = materialChances.next();
        chance.getMaterials().forEach(mat -> itemsGained.add(new ItemStack(mat, ThreadLocalRandom.current().nextInt(chance.getMinimumAmount(),
                chance.getMaximumAmount()))));
    }

    protected void insertResources() {
        machine.getResourcesGained().addAll(itemsGained);
    }

    /**
     * Uses MaterialChance class to define materials given for a specific chance, in a specific range
     */
    private void initializeMaterialChances() {
        materialChances = new RandomCollection<>();
        switch (machine.machineType) {
            case LUMBERJACK:
                addLumberjackMaterialChances();
                break;
            case EXCAVATOR:
                addExcavatorMaterialChances();
                break;
            case MINER:
                addMinerMaterialChances();
                break;
            case FARMER:
                addFarmerMaterialChances();
                break;
            case ALL:
                addMinerMaterialChances();
                addFarmerMaterialChances();
                addExcavatorMaterialChances();
                addLumberjackMaterialChances();
                break;
        }
    }

    private void addMinerMaterialChances() {
        materialChances.add(3, new MaterialChance(Material.DIAMOND, (int) Math.floor(2 * lootAmplifier), (int) Math.floor(3 * lootAmplifier)));
        materialChances.add(5, new MaterialChance(Material.DIAMOND_ORE, (int) Math.floor(1 * lootAmplifier), (int) Math.floor(2 * lootAmplifier)));
        materialChances.add(10, new MaterialChance(Material.GOLD_ORE, (int) Math.floor(3 * lootAmplifier), (int) Math.floor(5 * lootAmplifier)));
        materialChances.add(30, new MaterialChance(Material.IRON_ORE, (int) Math.floor(5 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
        materialChances.add(50, new MaterialChance((int) Math.floor(3 * lootAmplifier), (int) Math.floor(8 * lootAmplifier), Material.COAL, Material.IRON_INGOT));
        materialChances.add(90, new MaterialChance((int) Math.floor(5 * lootAmplifier), (int) Math.floor(15 * lootAmplifier), Material.COBBLESTONE, Material.STONE));
    }

    private void addFarmerMaterialChances() {
        materialChances.add(100, new MaterialChance(new ArrayList<>(Tag.CROPS.getValues()), (int) Math.floor(3 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
    }

    private void addExcavatorMaterialChances() {
        materialChances.add(1, new MaterialChance(Material.GRASS_BLOCK, (int) Math.floor(5 * lootAmplifier), (int) Math.floor(7 * lootAmplifier)));
        materialChances.add(3, new MaterialChance(Material.DIRT, (int) Math.floor(8 * lootAmplifier), (int) Math.floor(10 * lootAmplifier)));
    }

    private void addLumberjackMaterialChances() {
        MaterialChance chance = new MaterialChance((int) Math.floor(5 * lootAmplifier), (int) Math.floor(10 * lootAmplifier));
        chance.getMaterials().addAll(Tag.LOGS.getValues());
        materialChances.add(100, chance);
    }

    public RandomCollection<MaterialChance> getMaterialChances() {
        return materialChances;
    }

    public double getLootAmplifier() {
        return lootAmplifier;
    }

    public void setLootAmplifier(double lootAmplifier) {
        this.lootAmplifier = lootAmplifier;
    }

    public List<ItemStack> getItemsGained() {
        return itemsGained;
    }

    public void setItemsGained(List<ItemStack> itemsGained) {
        this.itemsGained = itemsGained;
    }

    public PlayerMachine getMachine() {
        return machine;
    }

    public void setMachine(PlayerMachine machine) {
        this.machine = machine;
    }

    public int getMinePeriod() {
        return minePeriod;
    }

    public void setMinePeriod(int minePeriod) {
        this.minePeriod = minePeriod;
    }

}

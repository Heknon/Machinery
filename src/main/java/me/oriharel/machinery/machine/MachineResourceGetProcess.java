package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.data.Chanceable;
import me.oriharel.machinery.data.ChanceableOperation;
import me.oriharel.machinery.message.Message;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import me.oriharel.machinery.utilities.RandomCollection;
import me.oriharel.machinery.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MachineResourceGetProcess {
    private List<ItemStack> itemsGained;
    private long zenCoinsGained;
    private PlayerMachine machine;
    private int minePeriod;
    private BukkitRunnable process;
    private RandomCollection<ChanceableOperation<?, MachineResourceGetProcess>> chanceables;
    private double lootAmplifier;

    public MachineResourceGetProcess(PlayerMachine machine) {
        itemsGained = new ArrayList<>();
        this.machine = machine;
        this.minePeriod = 20;
        this.chanceables = null;
        this.process = null;
        this.lootAmplifier = 1;
        this.zenCoinsGained = 0;
        initializeMaterialChances();
    }

    public void startProcess() {
        List<AbstractUpgrade> upgrades = machine.getUpgrades();
        upgrades.forEach(upgrade -> upgrade.applyUpgradeModifier(this));
        process = new BukkitRunnable() {
            @Override
            public void run() {
                if (machine.getEnergyInMachine() < machine.getFuelDeficiency()) {
                    cancel();
                    Player player = Bukkit.getPlayer(machine.getOwner());
                    if (player == null || !player.isOnline()) return;
                    new Message("messages.yml", "not_enough_fuel_to_operate", player, Utils.getLocationPlaceholders(machine.getMachineCore(),
                            Utils.getMachinePlaceholders(machine)));
                    return;
                }
                getResources();
                upgrades.forEach(upgrade -> {
                    if (!upgrade.isRunOnlyOnProcessStart()) upgrade.applyUpgradeModifier(MachineResourceGetProcess.this);
                });
                insertResources();
                runFuelRemoval();
            }
        };
        process.runTaskTimerAsynchronously(Machinery.getInstance(), minePeriod, minePeriod);
    }

    public void endProcess() {
        process.cancel();
    }

    public boolean isRunning() {
        return !process.isCancelled();
    }

    private void getResources() {
        if (chanceables == null) {
            initializeMaterialChances();
        }

        ChanceableOperation<?, MachineResourceGetProcess> chance = chanceables.next();
        chance.executeChanceOperation(this);
    }

    private void runFuelRemoval() {
        machine.removeEnergy(machine.getFuelDeficiency());
    }

    private void insertResources() {
        HashMap<Material, ItemStack> machineResourcesGained = machine.getResourcesGained();
        AtomicReference<Double> totalAmount = new AtomicReference<>((double) 0);
        itemsGained.forEach(
                item -> {
                    totalAmount.updateAndGet(v -> v + item.getAmount());
                    if (machineResourcesGained.containsKey(item.getType())) {
                        ItemStack prev = machineResourcesGained.get(item.getType());
                        prev.setAmount(prev.getAmount() + item.getAmount());
                        return;
                    }
                    machineResourcesGained.put(item.getType(), item.clone());
                }
        );
        machine.setTotalResourcesGained(machine.getTotalResourcesGained() + totalAmount.get());
        machine.addZenCoinsGained(this.zenCoinsGained);
        this.zenCoinsGained = 0;
    }

    /**
     * Uses MaterialChance class to define materials given for a specific chance, in a specific range
     */
    private void initializeMaterialChances() {
        chanceables = new RandomCollection<>();
        ResourceMap resourceMap = machine.getFactory().getMachinery().getMachineManager().getMachineResourceTrees().get(machine.machineName);
        for (Map.Entry<Integer, ChanceableOperation<?, MachineResourceGetProcess>> weightChanceableEntry : resourceMap.entrySet()) {
            int weight = weightChanceableEntry.getKey();
            chanceables.add(weight, weightChanceableEntry.getValue());
        }
    }

    public RandomCollection<ChanceableOperation<?, MachineResourceGetProcess>> getChanceables() {
        return chanceables;
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

    public void addZenCoinsGained(long amount) {
        zenCoinsGained += amount;
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

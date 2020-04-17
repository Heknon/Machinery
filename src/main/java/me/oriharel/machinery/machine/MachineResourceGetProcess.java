package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MachineResourceGetProcess {
    protected List<ItemStack> itemsGained;
    protected PlayerMachine machine;
    protected int minePeriod;
    protected List<Material> resourceCache;
    protected Random random;

    public MachineResourceGetProcess(PlayerMachine machine) {
        itemsGained = new ArrayList<>();
        this.machine = machine;
        this.minePeriod = 20;
        this.resourceCache = null;
        this.random = new Random();
    }

    public void startProcess() {
        final List<AbstractUpgrade> upgrades = machine.getUpgrades();
        upgrades.forEach(upgrade -> upgrade.applyUpgradeModifier(MachineResourceGetProcess.this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(Machinery.getInstance(), () -> {
            getResources();
            upgrades.forEach(upgrade -> upgrade.applyUpgradeModifier(MachineResourceGetProcess.this));
        }, 0, minePeriod);
    }

    public void endProcess() {

    }

    protected void getResources() {
        List<Material> resourceCache = this.resourceCache;
        Random random = this.random;

        if (resourceCache == null) {
            return;
        }
        int chance = random.nextInt(100);
        if (chance < 5) {
            itemsGained.addAll(resourceCache.stream().map(mat -> new ItemStack(mat, random.nextInt(14) + 1)).collect(Collectors.toSet()));
        } else if (chance < 50) {
            itemsGained.addAll(resourceCache.stream().map(mat -> new ItemStack(mat, random.nextInt(9) + 1)).collect(Collectors.toSet()));
        } else if (chance < 100) {
            itemsGained.addAll(resourceCache.stream().map(mat -> new ItemStack(mat, random.nextInt(4) + 1)).collect(Collectors.toSet()));
        }

    }

    protected void insertResources() {
        machine.getResourcesGained().addAll(itemsGained);
    }


}

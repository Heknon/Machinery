package me.oriharel.machinery.machine;

import me.oriharel.machinery.Machinery;
import me.oriharel.machinery.upgrades.AbstractUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MachineResourceGetProcess {
    protected List<ItemStack> itemsGained;
    protected int timeLeft;
    protected PlayerMachine machine;

    public MachineResourceGetProcess(PlayerMachine machine) {
        itemsGained = new ArrayList<>();
        timeLeft = 100;
        this.machine = machine;
    }

    public void startProcess() {
        final List<AbstractUpgrade> upgrades = machine.getUpgrades();
        Bukkit.getScheduler().runTaskTimerAsynchronously(Machinery.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                timeLeft--;
                upgrades.forEach(upgrade -> upgrade.applyUpgradeModifier(MachineResourceGetProcess.this));
                getResources();
                if (timeLeft == 0) {
                    cancel();
                    upgrades.forEach(upgrade -> upgrade.applyUpgradeModifier(MachineResourceGetProcess.this));
                }
            }
        }, 0, 20);
    }

    public void getResources() {

    }


}

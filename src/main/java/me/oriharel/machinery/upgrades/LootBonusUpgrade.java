package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.stream.Collectors;

public class LootBonusUpgrade extends AbstractUpgrade {

    private Random random;

    public LootBonusUpgrade(int level) {
        super(level);
        this.runOnlyOnProcessStart = true;
        this.random = new Random();
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {
        mineProcess.setLootAmplifier(level);
    }

    @Override
    public String getUpgradeName() {
        return "Loot Bonus Upgrade";
    }

    @Override
    public String toString() {
        return "LootBonusUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}';
    }
}

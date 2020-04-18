package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

public class LootBonusUpgrade extends AbstractUpgrade {

    public LootBonusUpgrade(int level) {
        super(level);
        this.runOnlyOnProcessStart = true;
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

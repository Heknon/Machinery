package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

public class LootBonusUpgrade extends AbstractUpgrade {

    public LootBonusUpgrade(int level) {
        super(level);
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {

    }

    @Override
    public String getUpgradeName() {
        return "Loot Bonus Upgrade";
    }
}

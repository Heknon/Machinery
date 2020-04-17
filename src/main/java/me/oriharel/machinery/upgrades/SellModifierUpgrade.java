package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

public class SellModifierUpgrade extends AbstractUpgrade {

    public SellModifierUpgrade(int level) {
        super(level);
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {

    }

    @Override
    public String getUpgradeName() {
        return "Sell Modifier Upgrade";
    }
}

package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

public class SpeedUpgrade extends AbstractUpgrade {

    public SpeedUpgrade(int level) {
        super(level);
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {

    }

    @Override
    public String getUpgradeName() {
        return "Speed Upgrade";
    }
}

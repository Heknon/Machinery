package me.oriharel.machinery.upgrades;

import me.oriharel.machinery.machine.MachineResourceGetProcess;

public class SpeedUpgrade extends AbstractUpgrade {

    public SpeedUpgrade(int level) {
        super(level);
    }

    @Override
    public void applyUpgradeModifier(MachineResourceGetProcess mineProcess) {
        mineProcess.setMinePeriod(200 / level);
        runOnlyOnProcessStart = true;
    }

    @Override
    public String getUpgradeName() {
        return "Speed Upgrade";
    }

    @Override
    public String toString() {
        return "SpeedUpgrade{" +
                "level=" + level +
                ", runOnlyOnProcessStart=" + runOnlyOnProcessStart +
                '}';
    }
}
